package com.herringbone.stock.service;

import com.herringbone.stock.domain.YahooQuoteBean;
import com.herringbone.stock.model.PeriodTrend;
import com.herringbone.stock.model.QuoteBase;
import com.herringbone.stock.model.Ticker;
import com.herringbone.stock.model.TrendBase;
import com.herringbone.stock.model.Trendtype;
import com.herringbone.stock.repository.TrendTypeRepository;
import com.herringbone.stock.util.Constants;
import com.herringbone.stock.util.Period;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Slf4j
@Service
public class QuoteProcessingService<T extends TrendBase & PeriodTrend> {

    private static final String PATTERN = "###.##";
    private static final int WINDOW_SIZE = 20;

    private final TickerService tickerService;
    private final TrendTypeRepository trendtypeRepository;
    private final QuoteLoadingFactory quoteLoadingFactory;

    public QuoteProcessingService(TickerService tickerService,
                                  TrendTypeRepository trendtypeRepository,
                                  QuoteLoadingFactory quoteLoadingFactory) {
        this.tickerService = tickerService;
        this.trendtypeRepository = trendtypeRepository;
        this.quoteLoadingFactory = quoteLoadingFactory;
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void processQuote(YahooQuoteBean yahooQuote, Supplier<QuoteBase> quoteMapper, Period period, Class<T> trendClazz) {
        Ticker ticker = tickerService.getTicker(yahooQuote.getSymbol());

        Double lastClose;
        // Get the last quote from the database!
        QuoteBase lastQuote;
        lastQuote = quoteLoadingFactory.getLoader(period).findLatestQuote(ticker.getId());
        if (lastQuote == null) {
            lastClose = 0.0;
        } else {
            lastClose = lastQuote.getClose();
        }

        // make sure that the new quote does not have the same date as the last
        // loaded quote
        if (quoteLoadingFactory.getLoader(period).quoteGatingRule(yahooQuote, lastQuote)) {
            log.info("Saving {} quote: {}", period.name(), yahooQuote.toString());
            // Determine what the new trend is going to be
            Double close = yahooQuote.getClose();
            if (close == null) {
                close = yahooQuote.getAdjClose();
            }
            if (close == null) {
                close = yahooQuote.getLast();
            }
            double trendChange = close - lastClose;
            int latestTrendType = 0;
            if (trendChange > 0.0)
                latestTrendType = Constants.UPTREND;
            else if (trendChange < 0.0)
                latestTrendType = Constants.DOWNTREND;

            //Create current quote to save
            QuoteBase currentQuote = quoteMapper.get();
            currentQuote.setTicker(ticker);
            currentQuote.setDate(yahooQuote.getDate());
            currentQuote.setTimeentered(ZonedDateTime.now());
            currentQuote.setClose(close); //Sometimes this is null, if so, then use the "last" field.
            currentQuote.setAdjclose(yahooQuote.getAdjClose());
            double periodPointChange = close - yahooQuote.getOpen();
            int periodClosingType = Constants.UNCHANGED;
            if (periodPointChange > 0.0)
                periodClosingType = 2; //An up day/week/month
            else if (periodPointChange < 0.0)
                periodClosingType = 1; //A down day/week/month

            currentQuote.setPeriodType(trendtypeRepository.findByTrendvalue(periodClosingType).get(0));
            currentQuote.setTrendtype(trendtypeRepository.findByTrendvalue(latestTrendType).get(0));
            if (lastQuote != null) {
                Long id = lastQuote.getId();
                QuoteBase lastBasicQuote = quoteLoadingFactory.getLoader(period).findBasicQuote(id);
                currentQuote.setPrevPeriod(lastBasicQuote);
                currentQuote.setLogchange(Math.log(close
                        / lastQuote.getClose()));
                Double stdDevLogChange = getStdDevLogChange(currentQuote.getLogchange(),
                        WINDOW_SIZE, false, ticker, period);
                Double oneStdDev = lastQuote.getClose() * stdDevLogChange;
                Double dayToDayChange = currentQuote.getClose() - lastQuote.getClose();
                Double spike = dayToDayChange / oneStdDev;
                currentQuote.setSpike(spike);
                lastQuote.setNextPeriod(quoteLoadingFactory.getLoader(period).findBasicQuote(currentQuote.getId()));
                quoteLoadingFactory.getLoader(period).saveQuote(lastQuote);
                currentQuote.setVolatility(calculateVolatility(currentQuote.getLogchange(), ticker, period));
            }

            quoteLoadingFactory.getLoader(period).saveQuote(currentQuote);
            updateTrend(ticker, period, trendClazz);

        }
        else {
            log.info("Attempt to save invalid {} quote {}. Last stored quote {}", period.name(), yahooQuote.toString(), lastQuote.getDate());
        }
        log.info("Done loading {} quotes", period.name());
    }

    private void updateTrend(Ticker ticker, Period period, Class<T> periodTrend) {
        try {
            T latestTrend = (T) quoteLoadingFactory.getLoader(period).findLatestTrend(ticker.getId());
            Trendtype latestTrendType = new Trendtype();
            QuoteBase latestTrendEnd;
            int latestTrendPeriodsInCount = 0;
            Double endClose = 0.0;
            QuoteBase currentQuote = quoteLoadingFactory.getLoader(period).findLatestQuote(ticker.getId());
            Trendtype currentQuoteTrendType = currentQuote.getTrendtype();
            Double currentClose = currentQuote.getClose();
            Double currentOpen = currentQuote.getOpen();

            if (latestTrend != null) {
                latestTrendType = latestTrend.getTrendtype();
                latestTrendEnd = (QuoteBase) latestTrend.getTrendend();
                latestTrendPeriodsInCount = latestTrend.getPeriodsInTrendCount();
                if (latestTrendEnd != null) {
                    endClose = latestTrendEnd.getClose();
                }
                // I want to count nochange days as part of the previous trend
                if (currentQuoteTrendType.getTrendvalue() == 0) {
                    currentQuoteTrendType = latestTrendType;
                }
            } else {
                endClose = currentOpen;
            }

            if (latestTrendType.equals(currentQuoteTrendType)
                    && latestTrend != null) {
                latestTrend.setTrendend(currentQuote);
                latestTrend.setPeriodsInTrendCount(latestTrendPeriodsInCount + 1);
                QuoteBase firstQuote = (QuoteBase) latestTrend.getTrendstart();
                Double pointChange = currentClose - firstQuote.getClose();
                DecimalFormat formatter = new DecimalFormat(PATTERN);
                String formattedPointChange = formatter.format(pointChange);
                pointChange = Double.valueOf(formattedPointChange);
                latestTrend.setTrendpointchange(pointChange);
                // I think this is a bug here, we should be getting the start close
                // of the trend
                // to generate the percentage change instead of the end close.
                // this will also require all trend percents to be recalculated as
                // well
                Double percentageChange = pointChange / firstQuote.getClose();
                latestTrend.setTrendpercentagechange(percentageChange);
                quoteLoadingFactory.getLoader(period).saveTrend(latestTrend);
            }
            // now we have a new trend starting and an old trend ending
            else {
                T trend = periodTrend.getDeclaredConstructor().newInstance();
                trend.setTrendtype(currentQuoteTrendType);
                if (latestTrend != null) {
                    trend.setTrendstart(latestTrend.getTrendend());
                } else {
                    trend.setTrendstart(currentQuote);
                }
                trend.setTrendend(currentQuote);
                trend.setTicker(ticker);
                if (latestTrend != null) {
                    trend.setPrevioustrend(latestTrend);
                }
                trend.setPeriodsInTrendCount(1);
                double pointChange = currentQuote.getClose() - endClose;
                DecimalFormat formatter = new DecimalFormat(PATTERN);
                String formattedPointChange = formatter.format(pointChange);
                pointChange = Double.parseDouble(formattedPointChange);
                trend.setTrendpointchange(pointChange);
                Double percentageChange = trend.getTrendpointchange() / endClose;
                trend.setTrendpercentagechange(percentageChange);
                quoteLoadingFactory.getLoader(period).saveTrend(trend);
                if (latestTrend != null) {
                    latestTrend.setNexttrend(trend);
                    quoteLoadingFactory.getLoader(period).saveTrend(latestTrend);
                }
            }
        } catch (Exception e) {
            log.info("error {}", e.getMessage(), e);
        }
    }

    private Double getStdDevLogChange(Double logchange, Integer windowSize, Boolean useCurrentQuote, Ticker ticker, Period period) {
        DescriptiveStatistics logMeanWindowStats = new DescriptiveStatistics();
        Integer numberQuotesToGet = windowSize;
        if (useCurrentQuote) {
            numberQuotesToGet = windowSize - 1;
        }
        List<QuoteBase> results = quoteLoadingFactory.getLoader(period).findQuotesInRange(ticker.getId(), numberQuotesToGet);
        DescriptiveStatistics devSquared = new DescriptiveStatistics();
        results.stream().filter( r -> r.getLogchange() != null).map( r ->  {
            logMeanWindowStats.addValue(r.getLogchange());
            return r.getLogchange();
        }).collect(Collectors.collectingAndThen(Collectors.toList(), list -> {
            if (useCurrentQuote) {
                list.add(logchange);
            }
            return list;
        }
        )).forEach(r-> devSquared.addValue(Math.pow(r - logMeanWindowStats.getMean(), 2)));
        return Math.sqrt(devSquared.getSum()
                / (logMeanWindowStats.getValues().length - 1));
    }

    private Double calculateVolatility(Double logchange, Ticker ticker, Period period) {
        Double volatility = getStdDevLogChange(logchange, WINDOW_SIZE - 1, true, ticker, period);
        if (volatility.equals(Double.NaN)) {
            volatility = 0.00938790405421897;
        }
        return volatility;
    }
}
