package com.herringbone.stock.service;

import com.herringbone.stock.domain.Trend;
import com.herringbone.stock.domain.YahooQuoteBean;
import com.herringbone.stock.model.MonthlyBasicQuote;
import com.herringbone.stock.model.MonthlyQuote;
import com.herringbone.stock.model.Monthlytrend;
import com.herringbone.stock.model.PeriodTrend;
import com.herringbone.stock.model.QuoteBase;
import com.herringbone.stock.model.Ticker;
import com.herringbone.stock.model.TrendBase;
import com.herringbone.stock.repository.MonthlyQuoteRepository;
import com.herringbone.stock.repository.MonthlyTrendRepository;
import com.herringbone.stock.util.ZonedDateTracker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

;
@Slf4j
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
    public QuoteBase saveQuote(QuoteBase quote) {
        return monthlyQuoteRepository.saveAndFlush((MonthlyQuote)quote);
    }

    @Override
    public void updateQuote(QuoteBase quote, Long id) {
        monthlyQuoteRepository.updateQuote((MonthlyBasicQuote)quote, id);
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
    public TrendBase saveTrend(PeriodTrend trend) {
        return monthlyTrendRepository.saveAndFlush((Monthlytrend)trend);
    }

    @Override
    public void updateTrend(PeriodTrend trend, QuoteBase currentQuote) {
        Monthlytrend monthlytrend = (Monthlytrend)trend;
        MonthlyQuote monthlyQuote = (MonthlyQuote)currentQuote;
        if (monthlyQuote.getTrendtype().equals(monthlytrend.getTrendtype())) {
            monthlyTrendRepository.updateTrend(monthlytrend.getMonthsintrendcount(), monthlytrend.getTrendpercentagechange(),
                    monthlytrend.getTrendpointchange(), monthlyQuote, monthlytrend.getId());
            log.info("Updating trend {} for ticker {} ", monthlytrend.getId(), monthlyQuote.getTicker().getSymbol());
        } else {
            log.info("Failed to Update trend {} for ticker {} ", monthlytrend.getId(), monthlyQuote.getTicker().getSymbol());
            throw new RuntimeException("Invalid update to the trend");
        }
    }

    @Override
    public void updatePreviousTrend(PeriodTrend nextTrend, Long trendId) {

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
