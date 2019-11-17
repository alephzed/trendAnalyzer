package com.herringbone.stock.service;

import com.herringbone.stock.domain.Trend;
import com.herringbone.stock.model.HistoricalTrendElement;
import com.herringbone.stock.model.Ticker;

import java.util.stream.Stream;

public interface StockFindService {
    Double getLatestVolatility();
    Stream<HistoricalTrendElement> findHistoricalMatchingTrends(Trend trend,
                                                                Integer precision, Double currentTrendVolatility, Ticker ticker);
    Long findSimilarTrendsCount(Trend trend, Integer precision, Double currentTrendVolatility, Ticker ticker);
    Double findMaxNextImpulseMove(Trend trend, Integer precision, Double currentTrendVolatility, Ticker ticker);
    Double findMinNextImpulseMove(Trend trend, Integer precision, Double currentTrendVolatility, Ticker ticker);
}
