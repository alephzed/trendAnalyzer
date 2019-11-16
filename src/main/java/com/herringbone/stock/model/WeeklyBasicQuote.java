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
@Table(name = "weeklyquote")
public class WeeklyBasicQuote extends QuoteBase implements java.io.Serializable {

    @Column(name = "PREVWEEKID")
    private Long prevweek;

    @Column(name = "NEXTWEEKID")
    private Long nextweek;

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn( name = "WEEKTYPE", referencedColumnName = "TRENDVALUE")
    @Fetch(FetchMode.JOIN)
    private Trendtype weektype;

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinColumn( name = "WEEKDAYSTARTID", referencedColumnName = "ID")
    @JsonSerialize(using = CustomQuoteSerializer.class)
    private DailyBasicQuote weekstartday;

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinColumn( name = "WEEKDAYENDID", referencedColumnName = "ID")
    @JsonSerialize(using = CustomQuoteSerializer.class)
    private DailyBasicQuote weekendday;

    private static final long serialVersionUID = -3994007977478642037L;

    public Trendtype getWeektype() {
        return this.weektype;
    }

    public void setWeektype(Trendtype daytype) {
        this.weektype = daytype;
    }

    public Long getPrevweek() {
        return prevweek;
    }

    public void setPrevweek(Long prevday) {
        this.prevweek = prevday;
    }

    public Long getNextweek() {
        return nextweek;
    }

    public void setNextweek(Long nextday) {
        this.nextweek = nextday;
    }

    public DailyBasicQuote getWeekstartday() {
        return weekstartday;
    }

    public void setWeekstartday(DailyBasicQuote weekstartday) {
        this.weekstartday = weekstartday;
    }

    public DailyBasicQuote getWeekendday() {
        return weekendday;
    }

    public void setWeekendday(DailyBasicQuote weekendday) {
        this.weekendday = weekendday;
    }

}