package com.herringbone.stock.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "dailyquote")
// A non-recursive representation of the DailyQuote entity
@Getter
@Setter
public class DailyBasicQuote extends QuoteBase implements java.io.Serializable {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn( name = "DAYTYPE", referencedColumnName = "TRENDVALUE")
    @Fetch(FetchMode.JOIN)
    private Trendtype daytype;

    @Column(name = "PREVDAY")
    private Long prevday;

    @Column(name = "NEXTDAY")
    private Long nextday;

}
