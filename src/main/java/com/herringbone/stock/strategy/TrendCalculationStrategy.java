package com.herringbone.stock.strategy;

import com.herringbone.stock.domain.Range;
import com.herringbone.stock.model.HistoricalTrendElement;

public interface TrendCalculationStrategy {
    public Double getMinimumRangePrice(double startingPrice, Range trendRange);
    public Double getMaximumRangePrice(double startingPrice, Range trendRange);

    public Double getImpulsMoveStats(HistoricalTrendElement historicalTrendElement);
}
