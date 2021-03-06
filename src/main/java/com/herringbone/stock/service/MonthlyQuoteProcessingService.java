package com.herringbone.stock.service;

import com.herringbone.stock.domain.Trend;
import com.herringbone.stock.domain.YahooQuoteBean;
import com.herringbone.stock.model.MonthlyQuote;
import com.herringbone.stock.model.Monthlytrend;
import com.herringbone.stock.model.PeriodTrend;
import com.herringbone.stock.model.QuoteBase;
import com.herringbone.stock.model.Ticker;
import com.herringbone.stock.repository.MonthlyQuoteRepository;
import com.herringbone.stock.repository.MonthlyTrendRepository;
import com.herringbone.stock.util.ZonedDateTracker;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

;

@Service("Monthly")
public class MonthlyQuoteProcessingService implements QuoteLoader {
    private final MonthlyQuoteRepository monthlyQuoteRepository;
    private final MonthlyTrendRepository monthlyTrendRepository;
    private final ZonedDateTracker dateTracker;

    public MonthlyQuoteProcessingService(MonthlyQuoteRepository monthlyQuoteRepository, MonthlyTrendRepository monthlyTrendRepository, ZonedDateTracker dateTracker) {
        this.monthlyQuoteRepository = monthlyQuoteRepository;
        this.monthlyTrendRepository = monthlyTrendRepository;
        this.dateTracker = dateTracker;
    }

    @Override
    public MonthlyQuote findLatestQuote(Long tickerId) {
        return monthlyQuoteRepository.findFirstOneByTickerIdOrderByIdDesc(tickerId);
    }

    @Override
    public List findQuotesInRange(Long tickerId, Integer quantity) {
        return monthlyQuoteRepository.findByTickerIdOrderByDateDesc(tickerId, PageRequest.of(0, quantity));
    }

    @Override
    public void saveQuote(QuoteBase quote) {
        monthlyQuoteRepository.saveAndFlush((MonthlyQuote)quote);
    }

    @Override
    public QuoteBase findBasicQuote(Long id) {
        return monthlyQuoteRepository.findOne(id);
    }

    @Override
    public PeriodTrend findLatestTrend(Long tickerId) {
       return monthlyTrendRepository.findTop1ByTickerIdOrderByIdDesc(tickerId) ;
    }

    @Override
    public void saveTrend(PeriodTrend trend) {
        monthlyTrendRepository.saveAndFlush((Monthlytrend)trend);
    }

    @Override
    public Trend getTrend(Ticker ticker, Long directionId) {
        return monthlyTrendRepository.findMatchingTrend(directionId, ticker.getId())
                .stream().findFirst().orElse(Trend.EMPTY);
    }

    @Override
    public boolean quoteGatingRule(YahooQuoteBean yahooQuote, QuoteBase lastQuote) {
        if (lastQuote == null) {
            return true;
        }
        return dateTracker.isDifferentDate(yahooQuote.getDate(), lastQuote.getDate())
                && !(dateTracker.isMarketOpenNow() && dateTracker.isDateToday(yahooQuote.getDate())
                && dateTracker.inCurrentMonth(yahooQuote.getDate()))
                && !dateTracker.inCurrentMonth(yahooQuote.getDate());
    }
}
