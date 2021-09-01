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

import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
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
                currentQuote.setVolatility(calculateVolatility(currentQuote.getLogchange(), ticker, period));
            }
            QuoteBase quote = quoteLoadingFactory.getLoader(period).saveQuote(currentQuote);
            if (lastQuote != null) {
                quoteLoadingFactory.getLoader(period).updateQuote(quote, lastQuote.getId());
            }
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
            T updateTrend = periodTrend.getDeclaredConstructor().newInstance();
            if (latestTrendType.equals(currentQuoteTrendType)
                    && latestTrend != null) {
                updateTrend.setTrendend(currentQuote);
                updateTrend.setPeriodsInTrendCount(latestTrendPeriodsInCount + 1);
                QuoteBase firstQuote = (QuoteBase) latestTrend.getTrendstart();
                Double pointChange = currentClose - firstQuote.getClose();
                DecimalFormat formatter = new DecimalFormat(PATTERN);
                String formattedPointChange = formatter.format(pointChange);
                pointChange = Double.valueOf(formattedPointChange);
                updateTrend.setTrendpointchange(pointChange);
                // I think this is a bug here, we should be getting the start close
                // of the trend
                // to generate the percentage change instead of the end close.
                // this will also require all trend percents to be recalculated as
                // well
                Double percentageChange = pointChange / firstQuote.getClose();
                updateTrend.setTrendpercentagechange(percentageChange);
                updateTrend.setId(latestTrend.getId());
                updateTrend.setTrendtype(latestTrend.getTrendtype());
                quoteLoadingFactory.getLoader(period).updateTrend(updateTrend, currentQuote);
            }
            // now we have a new trend starting and an old trend ending
            else {
                T newTrend = periodTrend.getDeclaredConstructor().newInstance();
                newTrend.setTrendtype(currentQuoteTrendType);
                Optional.ofNullable(latestTrend).map( s->
                    {
                        newTrend.setTrendstart(s.getTrendend());
                        newTrend.setPrevioustrend(latestTrend);
                        return s;
                    });
                newTrend.setTrendend(currentQuote);
                newTrend.setTicker(ticker);
                newTrend.setPeriodsInTrendCount(1);
                double pointChange = currentQuote.getClose() - endClose;
                DecimalFormat formatter = new DecimalFormat(PATTERN);
                String formattedPointChange = formatter.format(pointChange);
                pointChange = Double.parseDouble(formattedPointChange);
                newTrend.setTrendpointchange(pointChange);
                Double percentageChange = newTrend.getTrendpointchange() / endClose;
                newTrend.setTrendpercentagechange(percentageChange);
                TrendBase savedTrend = quoteLoadingFactory.getLoader(period).saveTrend(newTrend);
                if (latestTrend != null) {
                    updateTrend.setNexttrend(savedTrend);
                    quoteLoadingFactory.getLoader(period).updatePreviousTrend((T)savedTrend, latestTrend.getId()); //this should be an update statement
                }
            }
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            log.info("error {}", e.getMessage(), e);
            throw new RuntimeException(e);
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
        return Math.sqrt(devSquared.getSum() / (logMeanWindowStats.getValues().length - 1));
    }

    private Double calculateVolatility(Double logchange, Ticker ticker, Period period) {
        Double volatility = getStdDevLogChange(logchange, WINDOW_SIZE - 1, true, ticker, period);
        if (volatility.equals(Double.NaN)) {
            volatility = 0.00938790405421897;
        }
        return volatility;
    }
}
