package com.herringbone.stock.strategy;

import com.herringbone.stock.domain.Range;
import com.herringbone.stock.model.HistoricalTrendElement;

public class UptrendCalculationStrategy implements TrendCalculationStrategy {

    @Override
    public Double getMinimumRangePrice(double startingPrice, Range trendRange) {
        return startingPrice -trendRange.getMinimumPercent() * startingPrice;
    }

    @Override
    public Double getMaximumRangePrice(double startingPrice, Range trendRange) {
        return startingPrice - trendRange.getMaximumPercent() * startingPrice;
    }

    @Override
    public Double getImpulsMoveStats(HistoricalTrendElement historicalTrendElement) {
        return 1 - historicalTrendElement.getNextImpulseMove( );
    }
}
