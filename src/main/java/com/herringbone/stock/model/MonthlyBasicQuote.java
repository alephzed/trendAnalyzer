package com.herringbone.stock.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.herringbone.stock.util.CustomQuoteSerializer;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "monthlyquote")
public class MonthlyBasicQuote extends QuoteBase implements java.io.Serializable {

    @Column(name = "PREVMONTHID")
    private
    Long prevmonth;

    @Column(name = "NEXTMONTHID")
    private
    Long nextmonth;

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn( name = "MONTHTYPE", referencedColumnName = "TRENDVALUE")
    @Fetch(FetchMode.JOIN)
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

    public Long getPrevmonth() {
        return prevmonth;
    }

    public void setPrevmonth(Long prevmonth) {
        this.prevmonth = prevmonth;
    }

    public Long getNextmonth() {
        return nextmonth;
    }

    public void setNextmonth(Long nextmonth) {
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