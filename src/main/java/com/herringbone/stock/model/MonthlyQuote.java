package com.herringbone.stock.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.herringbone.stock.util.CustomQuoteSerializer;
import com.herringbone.stock.util.CustomTrendSerializer;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "monthlyquote")
public class MonthlyQuote extends QuoteBase implements java.io.Serializable {

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @LazyToOne(LazyToOneOption.NO_PROXY)
    @JoinColumn(name = "PREVMONTHID")
    @JsonSerialize(using = CustomQuoteSerializer.class)
    private
    MonthlyBasicQuote prevmonth;

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinColumn(name = "NEXTMONTHID")
    @LazyToOne(LazyToOneOption.NO_PROXY)
    @JsonSerialize(using = CustomQuoteSerializer.class)
    private
    MonthlyBasicQuote nextmonth;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn( name = "MONTHTYPE", foreignKey = @ForeignKey(name = "MONTHTYPE_FK"), referencedColumnName = "TRENDVALUE")
    @JsonSerialize(using = CustomTrendSerializer.class)
    private
    Trendtype monthtype;

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinColumn( name = "MONTHDAYSTARTID", referencedColumnName = "ID")
    @JsonSerialize(using = CustomQuoteSerializer.class)
    private
    DailyBasicQuote monthstartday;

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinColumn( name = "MONTHDAYENDID", referencedColumnName = "ID")
    @JsonSerialize(using = CustomQuoteSerializer.class)
    private
    DailyBasicQuote monthendday;

    private static final long serialVersionUID = -3994007977478642037L;

    public Trendtype getMonthtype() {
        return this.monthtype;
    }

    public void setMonthtype(Trendtype monthtype) {
        this.monthtype = monthtype;
    }

    public MonthlyBasicQuote getPrevmonth() {
        return prevmonth;
    }

    public void setPrevmonth(MonthlyBasicQuote prevmonth) {
        this.prevmonth = prevmonth;
    }

    public MonthlyBasicQuote getNextmonth() {
        return nextmonth;
    }

    public void setNextmonth(MonthlyBasicQuote nextmonth) {
        this.nextmonth = nextmonth;
    }

    public DailyBasicQuote getMonthstartday() {
        return monthstartday;
    }

    public void setMonthstartday(DailyBasicQuote monthstartday) {
        this.monthstartday = monthstartday;
    }

    public DailyBasicQuote getMonthendday() {
        return monthendday;
    }

    public void setMonthendday(DailyBasicQuote monthendday) {
        this.monthendday = monthendday;
    }

}