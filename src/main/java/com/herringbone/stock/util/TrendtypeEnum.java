package com.herringbone.stock.util;

import lombok.Getter;

@Getter
public enum TrendtypeEnum {
    TYPE1("Downtrend where currentClose lt previousClose", 1),
    TYPE2("Uptrend where currentClose gt previousClose", 2),
    TYPE3("currentClose gt previousLow", 3),
    TYPE4("currentClose lt previousHigh", 4),
    TYPE5("open lt eq close", 5),
    TYPE6("open gt eq close", 6),
    TYPE7("low gt eq previouslow and high gt eq previoushigh", 7),
    TYPE8("low lt eq previouslow and high lt eq previoushigh", 8),
    TYPE0("unchanged", 0);

    private TrendtypeEnum(String description, Integer trendValue) {
        this.description = description;
        this.trendValue = trendValue;
    }

    private String description;
    private Integer trendValue;


}
