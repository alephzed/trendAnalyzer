package com.herringbone.stock.service;

import com.herringbone.stock.domain.Trend;
import com.herringbone.stock.domain.YahooQuoteBean;
import com.herringbone.stock.model.DailyQuote;
import com.herringbone.stock.model.Dailytrend;
import com.herringbone.stock.model.PeriodTrend;
import com.herringbone.stock.model.QuoteBase;
import com.herringbone.stock.model.Ticker;
import com.herringbone.stock.repository.DailyBasicQuoteRepository;
import com.herringbone.stock.repository.DailyQuoteRepository;
import com.herringbone.stock.repository.DailyTrendRepository;
import com.herringbone.stock.util.ZonedDateTracker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("Daily")
@Slf4j
public class DailyQuoteProcessingService implements QuoteLoader {
    private final DailyQuoteRepository dailyQuoteRepository;
    private final DailyBasicQuoteRepository dailyBasicQuoteRepository;
    private final DailyTrendRepository dailyTrendRepository;
    private final ZonedDateTracker dateTracker;

    public DailyQuoteProcessingService(DailyQuoteRepository dailyQuoteRepository, DailyBasicQuoteRepository dailyBasicQuoteRepository, DailyTrendRepository dailyTrendRepository, ZonedDateTracker dateTracker) {
        this.dailyQuoteRepository = dailyQuoteRepository;
        this.dailyBasicQuoteRepository = dailyBasicQuoteRepository;
        this.dailyTrendRepository = dailyTrendRepository;
        this.dateTracker = dateTracker;
    }

    @Override
    public DailyQuote findLatestQuote(Long tickerId) {
        return dailyQuoteRepository.findFirstOneByTickerIdOrderByIdDesc(tickerId);
    }

    @Override
    public List findQuotesInRange(Long tickerId, Integer quantity) {
        return dailyQuoteRepository.findByTickerIdOrderByDateDesc(tickerId, PageRequest.of(0, quantity));
    }

    @Override
    public void saveQuote(QuoteBase quote) {
        dailyQuoteRepository.saveAndFlush((DailyQuote)quote);
    }

    @Override
    public QuoteBase findBasicQuote(Long id) {
        return dailyBasicQuoteRepository.findOne(id);
    }

    @Override
    public PeriodTrend findLatestTrend(Long tickerId) {
       return dailyTrendRepository.findTop1ByTickerIdOrderByIdDesc(tickerId) ;
    }

    @Override
    public void saveTrend(PeriodTrend trend) {
        dailyTrendRepository.saveAndFlush((Dailytrend)trend);
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
        if (differentDate != true) {
            log.error("Cannot save {} because date is same as last date {}", yahooQuote.getDate(), lastQuote.getDate());
        }
        if (afterLastDate != true) {
            log.error("Cannot save {} because date is not after last date {}", yahooQuote.getDate(), lastQuote.getDate());
        }
        if (nextTradingDayAfterLastDate != true) {
            log.error("Cannot save {} because date is not the next trading day last date {}", yahooQuote.getDate(), lastQuote.getDate());
        }
        return differentDate && afterLastDate && nextTradingDayAfterLastDate;
    }
}
