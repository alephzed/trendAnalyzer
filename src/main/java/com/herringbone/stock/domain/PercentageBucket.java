package com.herringbone.stock.domain;

import com.herringbone.stock.model.HistoricalTrendElement;
import com.herringbone.stock.strategy.TrendCalculationStrategy;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Slf4j
public class PercentageBucket {
    private  @Singular
    SortedMap<Range, RangeDetail> buckets;
    private  double bottom;
    private  double top;
    private  double startingPrice;
    private  int rangeCount;
    private  double minimumInterval;
    private long trendMatchSize;

    public void insertRecord(Double percentageChange, HistoricalTrendElement matchingTrend) {
        Set<Range> ranges = buckets.keySet();
        DescriptiveStatistics volatilityStats = new DescriptiveStatistics();
        Double trendVolatility = matchingTrend.getTrendVolatility();
        boolean wasInserted = false;
        for (Range tempRange : ranges) {
            if (tempRange.contains(percentageChange)) {
                RangeDetail rangeDetail = buckets.get(tempRange);
                wasInserted = true;
                if (trendVolatility != null)
                    volatilityStats.addValue(trendVolatility);
                buckets.get(tempRange).setQuantity(rangeDetail.getQuantity() + 1);
                buckets.get(tempRange).setVolatility(volatilityStats.getMean());
                break;
            }
        }
        if (!wasInserted) {
            //TODO figure out why elements are not getting inserted, and what to do with those elements
            //This was happening when the trendbuckets were being initialized as NaN
            log.error("could not insert record: " + percentageChange);
        }
    }

    public Map<Range, RangeDetail> postProcess(TrendCalculationStrategy trendCalculationStrategy) {
        Float incrementalPercent = 0.0f;
        for (Map.Entry<Range, RangeDetail> bucketEntry : buckets.entrySet()) {
            RangeDetail rangeDetail = bucketEntry.getValue();
            Range range = bucketEntry.getKey();
            Double minVal = trendCalculationStrategy.getMinimumRangePrice(startingPrice, range);
            Double maxVal = trendCalculationStrategy.getMaximumRangePrice(startingPrice, range);
            Float percentOfTotal = (rangeDetail.getQuantity().floatValue() / Long.valueOf(trendMatchSize).floatValue());
            incrementalPercent += percentOfTotal;
            buckets.get(range).setRangeBottom(minVal);
            buckets.get(range).setRangeTop( maxVal );
            buckets.get(range).setPercentOfTotal(percentOfTotal);
            buckets.get(range).setIncrementalPercent(incrementalPercent);
            buckets.get(range).setMinimumPercent(range.getMinimumPercent());
            buckets.get(range).setMaximumPercent(range.getMaximumPercent());
        }
        return buckets;
    }
}
