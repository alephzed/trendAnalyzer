package com.herringbone.stock.strategy;

import com.herringbone.stock.domain.Range;
import com.herringbone.stock.model.HistoricalTrendElement;

public class DowntrendCalculationStrategy implements TrendCalculationStrategy {

    @Override
    public Double getMinimumRangePrice(double startingPrice, Range trendRange) {
        return startingPrice + trendRange.getMinimumPercent() * startingPrice;
    }

    @Override
    public Double getMaximumRangePrice(double startingPrice, Range trendRange) {
        return startingPrice + trendRange.getMaximumPercent() * startingPrice;
    }

    @Override
    public Double getImpulsMoveStats(HistoricalTrendElement historicalTrendElement) {
        return historicalTrendElement.getNextImpulseMove() - 1;
    }
}
