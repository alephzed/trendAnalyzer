package com.herringbone.stock.service;

import com.herringbone.stock.domain.Trend;
import com.herringbone.stock.domain.YahooQuoteBean;
import com.herringbone.stock.model.PeriodTrend;
import com.herringbone.stock.model.QuoteBase;
import com.herringbone.stock.model.Ticker;
import com.herringbone.stock.model.WeeklyQuote;
import com.herringbone.stock.model.Weeklytrend;
import com.herringbone.stock.repository.WeeklyBasicQuoteRepository;
import com.herringbone.stock.repository.WeeklyQuoteRepository;
import com.herringbone.stock.repository.WeeklyTrendRepository;
import com.herringbone.stock.util.ZonedDateTracker;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.util.List;

@Service("Weekly")
public class WeeklyQuoteProcessingService implements QuoteLoader {
    private final WeeklyQuoteRepository weeklyQuoteRepository;
    private final WeeklyBasicQuoteRepository weeklyBasicQuoteRepository;
    private final WeeklyTrendRepository weeklyTrendRepository;
    private final ZonedDateTracker dateTracker;

    public WeeklyQuoteProcessingService(WeeklyQuoteRepository weeklyQuoteRepository, WeeklyBasicQuoteRepository weeklyBasicQuoteRepository, WeeklyTrendRepository weeklyTrendRepository, ZonedDateTracker dateTracker) {
        this.weeklyQuoteRepository = weeklyQuoteRepository;
        this.weeklyBasicQuoteRepository = weeklyBasicQuoteRepository;
        this.weeklyTrendRepository = weeklyTrendRepository;
        this.dateTracker = dateTracker;
    }

    @Override
    public WeeklyQuote findLatestQuote(Long tickerId) {
        return weeklyQuoteRepository.findFirstOneByTickerIdOrderByIdDesc(tickerId);
    }

    @Override
    public List findQuotesInRange(Long tickerId, Integer quantity) {
        return weeklyQuoteRepository.findByTickerIdOrderByDateDesc(tickerId, PageRequest.of(0, quantity));
    }

    @Override
    public void saveQuote(QuoteBase quote) {
        weeklyQuoteRepository.saveAndFlush((WeeklyQuote)quote);
    }

    @Override
    public QuoteBase findBasicQuote(Long id) {
        return weeklyBasicQuoteRepository.findOne(id);
    }

    @Override
    public PeriodTrend findLatestTrend(Long tickerId) {
       return weeklyTrendRepository.findTop1ByTickerIdOrderByIdDesc(tickerId) ;
    }

    @Override
    public void saveTrend(PeriodTrend trend) {
        weeklyTrendRepository.saveAndFlush((Weeklytrend)trend);
    }

    @Override
    public Trend getTrend(Ticker ticker, Long directionId) {
        return weeklyTrendRepository.findMatchingTrend(directionId, ticker.getId())
                .stream().findFirst().orElse(Trend.EMPTY);
    }

    @Override
    public boolean quoteGatingRule(YahooQuoteBean yahooQuote,
                                   QuoteBase lastQuote) {
        return dateTracker.isDifferentDate(yahooQuote.getDate(), lastQuote.getDate())
                && dateTracker.isAfter(yahooQuote.getDate(), lastQuote.getDate())
                && yahooQuote.getDate().getDayOfWeek().equals(DayOfWeek.MONDAY)
                && !dateTracker.inCurrentWeek(yahooQuote.getDate());
    }
}
