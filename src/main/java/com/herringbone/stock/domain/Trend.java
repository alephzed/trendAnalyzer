package com.herringbone.stock.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.herringbone.stock.util.CustomDateTimeSerializer;
import com.herringbone.stock.util.Period;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Trend implements Serializable {

    @Id
    private Long trendId;
    private Double trendpointchange;
    private Double trendpercentagechange;
    private Double mean;
    private Double stdDev;
    private Double variance;
    private Double max;
    private Double min;
    private Double geoMean;
    private Integer precision;
    private String description;
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    private ZonedDateTime trendDate;
    private String symbol;
    private Period period;
    private Integer periodsInTrendCount;

    private Double beginning; //B1 or T1
    private Double middle; //T or B
    private Double end; //B2 or T2

    private Long beginning_id;
    private Long middle_id;
    private Long end_id;

    private String beginType; //B1 or T1
    private String middleType; //T or B
    private String endType; //B2 or T2
    private List<RangeDetail> ranges;

    public static final Trend EMPTY = new Trend();

    public Trend ( Long trendId, Double end, Double middle, Double beginning, Long end_id, Long beginning_id,
                   Long middle_id, ZonedDateTime trendDate, Integer periodsInTrendCount, Double trendpointchange,
                   Double trendpercentagechange) {
        this.trendId = trendId;
        this.end = end;
        this.middle = middle;
        this.beginning = beginning;
        this.end_id = end_id;
        this.beginning_id = beginning_id;
        this.middle_id = middle_id;
        this.trendDate = trendDate;
        this.periodsInTrendCount = periodsInTrendCount;
        this.trendpointchange = trendpointchange;
        this.trendpercentagechange = trendpercentagechange;
    }

}
