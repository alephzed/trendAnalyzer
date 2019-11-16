package com.herringbone.stock.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.herringbone.stock.util.CustomDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.ZonedDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class YahooQuoteBean implements Serializable {

    @JsonProperty("Date")
    private String dateStr;
    @JsonProperty("Open")
    private Double open;
    @JsonProperty("Close")
    private Double close;
    @JsonProperty("High")
    private Double high;
    @JsonProperty("Low")
    private Double low;
    @JsonProperty("Adj Close")
    private Double adjClose;
    @JsonProperty("Volume")
    private Long volume;
    private String symbol;

    //Below fields Added to support Google quote downloads
    private Double last;
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    private ZonedDateTime date;
    private String change;
}
