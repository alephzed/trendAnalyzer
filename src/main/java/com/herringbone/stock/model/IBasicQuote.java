package com.herringbone.stock.model;

import java.time.ZonedDateTime;

public interface IBasicQuote {
    Long getId();
    ZonedDateTime getDate();
    Double getOpen();
    Double getHigh();
    Double getLow();
    Double getClose();
    Long getVolume();
    Double getAdjclose();
    Trendtype getTrendtype();
    Double getLogchange();
    Double getVolatility();
    Double getSpike();
    Ticker getTicker();
//    Trendtype getPeriodType();
//    Long getPrevQuote();
//    Long getNextQuote();
}
