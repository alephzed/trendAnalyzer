package com.herringbone.stock.service;

import com.herringbone.stock.domain.Trend;
import com.herringbone.stock.domain.YahooQuoteBean;
import com.herringbone.stock.mapper.DailyQuoteMapper;
import com.herringbone.stock.model.DailyBasicQuote;
import com.herringbone.stock.model.DailyQuote;
import com.herringbone.stock.model.Dailytrend;
import com.herringbone.stock.model.IBasicQuote;
import com.herringbone.stock.model.PeriodTrend;
import com.herringbone.stock.model.QuoteBase;
import com.herringbone.stock.model.Ticker;
import com.herringbone.stock.model.TrendBase;
import com.herringbone.stock.model.Trendtype;
import com.herringbone.stock.repository.DailyQuoteRepository;
import com.herringbone.stock.repository.DailyTrendRepository;
import com.herringbone.stock.util.ZonedDateTracker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service("Daily")
@Slf4j
public class DailyQuoteProcessingService implements QuoteLoader {
    private final DailyQuoteRepository dailyQuoteRepository;
    private final DailyTrendRepository dailyTrendRepository;
    private final ZonedDateTracker dateTracker;
    private final DailyQuoteMapper dailyQuoteMapper;

    public DailyQuoteProcessingService(DailyQuoteRepository dailyQuoteRepository, DailyTrendRepository dailyTrendRepository,
                                       ZonedDateTracker dateTracker, DailyQuoteMapper dailyQuoteMapper) {
        this.dailyQuoteRepository = dailyQuoteRepository;
        this.dailyTrendRepository = dailyTrendRepository;
        this.dateTracker = dateTracker;
        this.dailyQuoteMapper = dailyQuoteMapper;
    }

    @Override
    public DailyQuote findLatestQuote(Long tickerId) {
        log.info("In findLatestQuote: Before findFirstOneByTickerIdOrderByIdDesc");
        DailyQuote dq = dailyQuoteRepository.findFirstOneByTickerIdOrderByIdDesc(tickerId);
        log.info("In findLatestQuote: After findFirstOneByTickerIdOrderByIdDesc");
        log.info("In findLatestQuote: Before findTop1ByTickerIdOrderByIdDesc");
        IBasicQuote bdq = dailyQuoteRepository.findTop1ByTickerIdOrderByIdDesc(tickerId);
        log.info("In findLatestQuote: After findTop1ByTickerIdOrderByIdDesc");
        return dq;
    }

    @Override
    public List findQuotesInRange(Long tickerId, Integer quantity) {
        return dailyQuoteRepository.findByTickerIdOrderByDateDesc(tickerId, PageRequest.of(0, quantity));
    }

    @Override
    public QuoteBase<DailyQuote> saveQuote(QuoteBase quote) {
        DailyQuote dQuote = (DailyQuote)quote;
        Trendtype dayType = dQuote.getDaytype();
        Double dayDiff = dQuote.getClose() - dQuote.getOpen();
        if ((dayDiff > 0 && dayType.getTrendvalue() == 2L ) ||
                (dayDiff < 0 && dayType.getTrendvalue() == 1L ) ||
                (dayDiff == 0 && dayType.getTrendvalue() == 0L)) {
            log.info("Saving quote for day {} symbol {}", dQuote.getDate(), dQuote.getTicker().getSymbol());
            return (QuoteBase)dailyQuoteRepository.saveAndFlush((DailyQuote) quote);
        } else {
            log.info("Unable to save quote for day {} symbol {}", dQuote.getDate(), dQuote.getTicker().getSymbol());
            throw new RuntimeException("Unable to save quote ");
        }
    }

    @Override
    public void updateQuote(QuoteBase quote, Long id) {
        DailyBasicQuote basicQuote = dailyQuoteMapper.dailyQuoteToBasicQuote((DailyQuote)quote);
        dailyQuoteRepository.updateQuote(basicQuote, id);
    }

    @Override
    public QuoteBase findBasicQuote(Long id) {
        log.info("Before findOne Daily");
        DailyBasicQuote dq = dailyQuoteRepository.findOne(id);
        log.info("After findOne Daily");
        return dq;
    }

    @Override
    public PeriodTrend findLatestTrend(Long tickerId) {
       return dailyTrendRepository.findTop1ByTickerIdOrderByIdDesc(tickerId) ;
    }

    @Override
    public TrendBase<DailyQuote, Dailytrend> saveTrend(PeriodTrend trend) {
        Dailytrend dailytrend = (Dailytrend) trend;
        Dailytrend previousTrend = Optional.ofNullable(dailytrend.getPrevioustrend())
                .orElse(Dailytrend.builder().trendtype(Trendtype.builder().trendvalue(-1).build())
                        .ticker(dailytrend.getTicker()).build());
        //validate
        DailyQuote quoteStart = Optional.ofNullable(dailytrend.getTrendstart())
                .orElse(DailyQuote.builder().close(0.0).ticker(dailytrend.getTicker()).build());
        DailyQuote quoteEnd = dailytrend.getTrendend();
        Double trendChange = quoteEnd.getClose() - quoteStart.getClose();
        long expectedTrendValue = 0L;
        if (trendChange > 0) {
            expectedTrendValue = 2L;
        } else if (trendChange < 0) {
            expectedTrendValue = 1L;
        }
        if (dailytrend.getTrendstart() == null) {
            dailytrend.setTrendstart(dailytrend.getTrendend());
        }
        if (quoteEnd.getTrendtype().equals(dailytrend.getTrendtype())
                && quoteEnd.getTrendtype().getTrendvalue() == expectedTrendValue
                && previousTrend.getTrendtype().getTrendvalue() != dailytrend.getTrendtype().getTrendvalue()) {
            log.info("Inserting a new trend for ticker {} and trend {}", quoteStart.getTicker().getSymbol(), dailytrend.getTrendtype().getTrendvalue());
            return dailyTrendRepository.save(dailytrend);
        } else
            log.info("Failed to Insert a new daily trend for ticker {} ", quoteStart.getTicker().getSymbol());
            throw new RuntimeException("Invalid insert to the trend");
        }

    }

    @Override
    public void updateTrend(PeriodTrend trend, QuoteBase currentQuote) {
        Dailytrend dailytrend = (Dailytrend)trend;
        DailyQuote dailyQuote = (DailyQuote)currentQuote;
        if (dailyQuote.getTrendtype().equals(dailytrend.getTrendtype()) || dailyQuote.getTrendtype().getTrendvalue() == 0) {
            Integer count = dailytrend.getDaysintrendcount();
            Double pctChange = dailytrend.getTrendpercentagechange();
            Double pointChange = dailytrend.getTrendpointchange();
            Long id = dailytrend.getId();
            dailyTrendRepository.updateTrend(count, pctChange,
                    pointChange, dailyQuote, id);
            log.info("Updating trend {} for ticker {} ", dailytrend.getId(), dailyQuote.getTicker().getSymbol());
        } else {
            log.info("Failed to Update trend {} for ticker {} ", dailytrend.getId(), dailyQuote.getTicker().getSymbol());
            throw new RuntimeException("Invalid update to the trend");
        }
    }

    @Override
    public void updatePreviousTrend(PeriodTrend nextTrend, Long trendId) {
        Dailytrend dailytrend = new Dailytrend();
        dailytrend.setId(((Dailytrend)nextTrend).getId());
        dailyTrendRepository.updateNextTrend(dailytrend, trendId);
    }

    @Override
    public Trend getTrend(Ticker ticker, Long directionId) {
        return dailyTrendRepository.findMatchingTrend(directionId, ticker.getId())
                .stream().findFirst().orElse(Trend.EMPTY);
    }

    @Override
    public boolean quoteGatingRule(YahooQuoteBean yahooQuote,
                                   QuoteBase lastQuote) {
        if (lastQuote == null) {
            return true;
        }
        boolean differentDate = dateTracker.isDifferentDate(yahooQuote.getDate(), lastQuote.getDate());
        boolean afterLastDate = dateTracker.isAfter(yahooQuote.getDate(), lastQuote.getDate());
        boolean nextTradingDayAfterLastDate = dateTracker.isNextTradingDay(yahooQuote.getDate(), lastQuote.getDate());
        if (!differentDate) {
            log.error("Cannot save {} because date is same as last date {}", yahooQuote.getDate(), lastQuote.getDate());
        }
        if (!afterLastDate) {
            log.error("Cannot save {} because date is not after last date {}", yahooQuote.getDate(), lastQuote.getDate());
        }
        if (!nextTradingDayAfterLastDate) {
            log.error("Cannot save {} because date is not the next trading day last date {}", yahooQuote.getDate(), lastQuote.getDate());
        }
        return differentDate && afterLastDate && nextTradingDayAfterLastDate;
    }
}
