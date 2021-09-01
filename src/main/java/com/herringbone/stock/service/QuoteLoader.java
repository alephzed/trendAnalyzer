package com.herringbone.stock.service;

import com.herringbone.stock.domain.Trend;
import com.herringbone.stock.domain.YahooQuoteBean;
import com.herringbone.stock.model.PeriodTrend;
import com.herringbone.stock.model.QuoteBase;
import com.herringbone.stock.model.Ticker;
import com.herringbone.stock.model.TrendBase;

import java.util.List;

public interface QuoteLoader {
    QuoteBase findLatestQuote(Long tickerId);
    List<QuoteBase> findQuotesInRange(Long tickerId, Integer quantity);
    QuoteBase saveQuote(QuoteBase quote);
    void updateQuote(QuoteBase quote, Long id);
    QuoteBase findBasicQuote(Long id);
    PeriodTrend findLatestTrend(Long tickerId);
    TrendBase saveTrend(PeriodTrend trend);
    void updateTrend(PeriodTrend trend, QuoteBase currentQuote);
    void updatePreviousTrend(PeriodTrend nextTrend, Long trendId);
    Trend getTrend(Ticker ticker, Long directionId);
    boolean quoteGatingRule(YahooQuoteBean yahooQuote, QuoteBase lastQuote);
}
