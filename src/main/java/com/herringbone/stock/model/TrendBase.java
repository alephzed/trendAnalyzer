package com.herringbone.stock.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.herringbone.stock.util.CustomQuoteSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import static javax.persistence.GenerationType.IDENTITY;

@MappedSuperclass
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TrendBase<T, V> implements java.io.Serializable {
    @Id
    @GeneratedValue(strategy=IDENTITY)
    @Column(name="ID", unique=true, nullable=false)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn( name = "TRENDTYPE", referencedColumnName = "TRENDVALUE")
    private Trendtype trendtype;

    @OneToOne(cascade = {CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinColumn( name = "TRENDSTARTID")
    @JsonSerialize(using = CustomQuoteSerializer.class)
    private T trendstart;

    @OneToOne(cascade = {CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinColumn( name = "TRENDENDID")
    @JsonSerialize(using = CustomQuoteSerializer.class)
    private T trendend;

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinColumn(name="PREVIOUSTRENDID")
    @JsonIgnore
    private V previoustrend;

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinColumn(name="NEXTTRENDID")
    @JsonIgnore
    private V nexttrend;

    @Column(name="TRENDPOINTCHANGE", precision=64)
    private Double trendpointchange;

    @Column(name="TRENDPERCENTAGECHANGE", precision=64)
    private Double trendpercentagechange;

//    @Column(name="SYMBOL", nullable=false, length=10)
//    private String symbol;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="TICKERID")
    Ticker ticker;

    @Transient
    private Integer periodsInTrendCount;
}
