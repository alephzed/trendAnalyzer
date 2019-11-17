package com.herringbone.stock.service;

import com.herringbone.stock.domain.Trend;
import com.herringbone.stock.domain.YahooQuoteBean;
import com.herringbone.stock.model.PeriodTrend;
import com.herringbone.stock.model.QuoteBase;
import com.herringbone.stock.model.Ticker;

import java.util.List;

public interface QuoteLoader {
    QuoteBase findLatestQuote(Long tickerId);
    List<QuoteBase> findQuotesInRange(Long tickerId, Integer quantity);
    void saveQuote(QuoteBase quote);
    QuoteBase findBasicQuote(Long id);
    PeriodTrend findLatestTrend(Long tickerId);
    void saveTrend(PeriodTrend trend);
    Trend getTrend(Ticker ticker, Long directionId);
    boolean quoteGatingRule(YahooQuoteBean yahooQuote, QuoteBase lastQuote);
}
