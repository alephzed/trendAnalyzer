package com.herringbone.stock.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.herringbone.stock.util.CustomDateTimeSerializer;
import com.herringbone.stock.util.CustomTrendSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.io.Serializable;
import java.time.ZonedDateTime;

import static javax.persistence.GenerationType.IDENTITY;

@MappedSuperclass
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuoteBase<T> implements Serializable{

    @Id
    @GeneratedValue(strategy=IDENTITY)
    @Column(name="ID", unique=true, nullable=false)
    @JsonProperty
    Long id;

    @Column(name = "DATE", nullable = false, length = 10)
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    private ZonedDateTime date;

    @Column(name = "OPEN", nullable = false, precision = 64)
    @JsonProperty
    private Double open;

    @Column(name = "HIGH", nullable = false, precision = 64)
    @JsonProperty
    private Double high;

    @Column(name = "LOW", nullable = false, precision = 64)
    @JsonProperty
    private Double low;

    @Column(name = "CLOSE", nullable = false, precision = 64)
    @JsonProperty
    private Double close;

    @Column(name = "VOLUME", nullable = false)
    @JsonProperty
    private Long volume;

    @Column(name = "ADJCLOSE", nullable = false, precision = 64)
    @JsonProperty
    private Double adjclose;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn( name = "TRENDTYPE") //, referencedColumnName = "TRENDVALUE")
    @JsonSerialize(using = CustomTrendSerializer.class)
    private Trendtype trendtype;

    @Column(name = "TIMEENTERED", length = 10)
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    private ZonedDateTime timeentered;

    @Column(name = "LOGCHANGE", precision = 64)
    @JsonProperty
    private Double logchange;

    @Column(name = "VOLATILITY", precision = 64)
    @JsonProperty
    private Double volatility;

    @Column(name = "SPIKE", precision = 64)
    @JsonProperty
    private Double spike;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="TICKERID")
    @JsonProperty
    private Ticker ticker;

    @Transient
    private Trendtype periodType;

    @Transient
    private T prevPeriod;

    @Transient
    private T nextPeriod;
}
