package com.herringbone.stock.model;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "dailyquote")
@Immutable
// A non-recursive representation of the DailyQuote entity
public class DailyBasicQuote extends QuoteBase implements java.io.Serializable {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn( name = "DAYTYPE", referencedColumnName = "TRENDVALUE")
    @Fetch(FetchMode.JOIN)
    private Trendtype daytype;

    @Column(name = "PREVDAY")
    private Long prevDay;

    @Column(name = "NEXTDAY")
    private Long nextDay;

}