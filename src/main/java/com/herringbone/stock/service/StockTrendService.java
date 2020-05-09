package com.herringbone.stock.service;

import com.herringbone.stock.domain.PercentageBucket;
import com.herringbone.stock.domain.Range;
import com.herringbone.stock.domain.RangeDetail;
import com.herringbone.stock.domain.Trend;
import com.herringbone.stock.exception.TrendException;
import com.herringbone.stock.model.HistoricalTrendElement;
import com.herringbone.stock.model.Ticker;
import com.herringbone.stock.strategy.DowntrendCalculationStrategy;
import com.herringbone.stock.strategy.TrendCalculationStrategy;
import com.herringbone.stock.strategy.UptrendCalculationStrategy;
import com.herringbone.stock.util.FibonacciRangeCalculator;
import com.herringbone.stock.util.Period;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentMap;

@Service
@Slf4j
public class StockTrendService {
    private static final int MINIMUM_TREND_SIZE = 2;
    private final TickerService tickerService;
    private final QuoteLoadingFactory quoteLoadingFactory;
    private final StockFindServiceFactory stockFindServiceFactory;
    private final FibonacciRangeCalculator fibonacciRangeCalculator;
    private final CacheManager cacheManager;


    public StockTrendService(TickerService tickerService,
                             QuoteLoadingFactory quoteLoadingFactory,
                             StockFindServiceFactory stockFindServiceFactory,
                             FibonacciRangeCalculator fibonacciRangeCalculator, CacheManager cacheManager) {
        this.tickerService = tickerService;
        this.quoteLoadingFactory = quoteLoadingFactory;
        this.stockFindServiceFactory = stockFindServiceFactory;
        this.fibonacciRangeCalculator = fibonacciRangeCalculator;
        this.cacheManager = cacheManager;
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    @Cacheable(value = "latestTrend", key = "{#symbolOrAlias, #direction, #period}")
    public Trend getLatestTrend(String symbolOrAlias, String direction, Period period, Integer precision) {
        log.info("Caching in latesttrend {}, {}, {}, {}", symbolOrAlias, direction, period, precision);
        Ticker ticker = tickerService.getTicker(symbolOrAlias);
        Trend trend = Trend.EMPTY;
        if (direction.equals("Up") || direction.equals("Both")) {
            trend = getTrend(ticker, period, "Up", 2L,  precision);
        }
        if (direction.equals("Down") || direction.equals("Both")) {
            trend = getTrend(ticker, period, "Down", 1L, precision);
        }
        return trend;
    }

    @CacheEvict(value = "latestTrend", allEntries = true)
    public void evictAllCacheValues() {
        log.info("Evicting all 'latestTrend' cache values");
    }

    private Trend getTrend(Ticker ticker, Period period, String direction, Long directionId, Integer precision) {
        Trend trendElement = quoteLoadingFactory.getLoader(period).getTrend(ticker, directionId);
        if (!precision.equals(trendElement.getPrecision())) {
            //TODO cache this
            trendElement.setPeriod(period);
            trendElement.setPrecision(precision);
            if (direction.equals("Up")) {
                trendElement.setBeginType("T1");
                trendElement.setMiddleType("B");
                trendElement.setEndType("T2");
                trendElement.setDescription("Use these price points to determine when to add to a long position or when to close out a short position");
                trendStatisticsPopulator(trendElement, new UptrendCalculationStrategy(), precision, ticker);
            }
            else if (direction.equals("Down")) {
                trendElement.setBeginType("B1");
                trendElement.setMiddleType("T");
                trendElement.setEndType("B2");
                trendElement.setDescription("Use these price points to determine when to add to a short position or when to close out a long position");
                trendStatisticsPopulator(trendElement, new DowntrendCalculationStrategy(), precision, ticker);
            }
        }
        return trendElement;
    }

    private void trendStatisticsPopulator(Trend trend, TrendCalculationStrategy trendCalculationStrategy, int precision, Ticker ticker) {
        log.trace("trendStatisticsPopulator");
        Double startingPrice = trend.getEnd();
        StockFindService stockFindService = stockFindServiceFactory.getLoader(trend.getPeriod().getFindService());
        Double latestVolatility = Objects.requireNonNull(stockFindService).getLatestVolatility();
        DescriptiveStatistics defaultTrendReversalPriceStats = new DescriptiveStatistics();
        DescriptiveStatistics defaultTrendNextImpulseMoveStats = new DescriptiveStatistics();

        //TODO why does this sometimes return NaN for the reversalPrice
        int trendPrecision = precision;
        Long historicalTrendElementsSize;
        Double maxImpulseMove;
        Double minImpulseMove;

        while (true) {
            historicalTrendElementsSize = stockFindService.findSimilarTrendsCount(trend, trendPrecision, latestVolatility, ticker);
            if (historicalTrendElementsSize > MINIMUM_TREND_SIZE) {
                maxImpulseMove =stockFindService.findMaxNextImpulseMove(trend, trendPrecision, latestVolatility, ticker);
                minImpulseMove = stockFindService.findMinNextImpulseMove(trend, trendPrecision, latestVolatility, ticker);
                break;
            } else if (trendPrecision == 10 ) {
                throw new TrendException("Unable to find matching historical trend for trend " + trend.toString());
            }
            trendPrecision++;
        }

        PercentageBucket trendBucket = getPercentageBucket(startingPrice, maxImpulseMove, minImpulseMove, historicalTrendElementsSize);

        log.info("Historical Trends {} {} size {} precision {}", trend.getPeriod().name(), trendCalculationStrategy.getClass().getName(),
                historicalTrendElementsSize , trendPrecision);
        stockFindService.findHistoricalMatchingTrends(trend, trendPrecision, latestVolatility, ticker)
                    .peek(dt -> {dt.setReversalPrice(calculateReversalPrice(dt, latestVolatility, trend.getEnd()));
                        Double reversalPrice = dt.getReversalPrice();
                        if (reversalPrice != null && !Double.isNaN(reversalPrice)) {
                            defaultTrendReversalPriceStats.addValue(reversalPrice);
                            defaultTrendNextImpulseMoveStats.addValue(trendCalculationStrategy.getImpulsMoveStats(dt));
                        } else {
                            log.info("Unable to update trend statistics");
                        }

                        Double percentChange = trendCalculationStrategy.getImpulsMoveStats(dt);
                        if (percentChange < 0) {
                            log.info("*********************************************************************************************************");
                            log.info("******************************Why is this less than 0? TODO - add more debugging This should NEVER happen**************************");
                            log.info("Cannot add trend to trendbucket {} with id: {}", trendBucket.toString(), dt.getTrendId() );
                            log.info("*********************************************************************************************************");
                        } else {
                            trendBucket.insertRecord(percentChange, dt);
                        }
                    }).count();

        Map<Range, RangeDetail> trendRange = trendBucket.postProcess(trendCalculationStrategy);
        trend.setRanges(new ArrayList<>(trendRange.values()));
        trend.setMean(defaultTrendReversalPriceStats.getMean());
        trend.setStdDev(defaultTrendReversalPriceStats.getStandardDeviation());
        trend.setVariance(defaultTrendReversalPriceStats.getVariance());
        trend.setMax(defaultTrendReversalPriceStats.getMax());
        trend.setMin(defaultTrendReversalPriceStats.getMin());
        trend.setGeoMean(defaultTrendReversalPriceStats.getGeometricMean());
    }

    public void findAll() {
        ((ConcurrentMap) cacheManager.getCache("latestTrend").getNativeCache()).keySet().stream()
                .forEach(s -> log.info("Keys From cache: " + s.toString()));
        ((ConcurrentMap) cacheManager.getCache("latestTrend").getNativeCache()).values().stream()
                .forEach(s -> log.info("Values From cache: " + s.toString()));
    }

    private PercentageBucket getPercentageBucket(Double startingPrice, Double maxImpulseMove, Double minImpulseMove,
                                                 Long historicalTrendElementsSize) {
        int rangeCount = 18;
        double minimumInterval = 0.001;
        PercentageBucket.PercentageBucketBuilder trendBucketBuilder = PercentageBucket.builder()
                .startingPrice(startingPrice).rangeCount(rangeCount)
                .minimumInterval(minimumInterval).trendMatchSize(historicalTrendElementsSize);
        List<Range> fibRanges = fibonacciRangeCalculator.getFibonacciRangeScaled(maxImpulseMove, minImpulseMove, historicalTrendElementsSize, rangeCount, minimumInterval);
        fibRanges.forEach( range -> trendBucketBuilder.bucket(range, new RangeDetail()));
        return trendBucketBuilder.build();
    }

    private double calculateReversalPrice(HistoricalTrendElement element, double currentTrendVolatility, double trendEnd) {
        return ((1 +( (element.getNextTrendClose() - element.getPrice3())/element.getPrice3()) * ( currentTrendVolatility / element.getTrendVolatility())) * trendEnd);
    }
}
