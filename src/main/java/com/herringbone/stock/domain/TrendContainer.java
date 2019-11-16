package com.herringbone.stock.domain;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class TrendContainer implements Serializable {
    Map<String, Trend> trend = new HashMap<>();

    private String trendPeriod;
    private String symbol;

    public void addTrend(String direction, Trend trend) {
        if (this.trend.containsKey(direction)) {
            this.trend.put(direction, trend);
        }
    }

    @Override
    public int hashCode() {
        int result = 7;
        result = 31 * result +  symbol.hashCode();
        result = 31 * result + trendPeriod.hashCode();
        return result;
    }
}
