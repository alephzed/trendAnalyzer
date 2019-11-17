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
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("Daily")
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
        return dailyQuoteRepository.findTop1ByTickerIdOrderByIdDesc(tickerId).get(0);
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
        return dateTracker.isDifferentDate(yahooQuote.getDate(), lastQuote.getDate())
                && dateTracker.isAfter(yahooQuote.getDate(), lastQuote.getDate())
                && dateTracker.isNextTradingDay(yahooQuote.getDate(), lastQuote.getDate());
    }
}
