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
@Table(name = "weeklyquote")
public class WeeklyQuote extends QuoteBase implements java.io.Serializable {

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.LAZY, optional = true)
    @LazyToOne(LazyToOneOption.NO_PROXY)
    @JoinColumn(name = "PREVWEEKID", nullable=true)
    @JsonSerialize(using = CustomQuoteSerializer.class)
    private WeeklyBasicQuote prevweek;

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "NEXTWEEKID", nullable=true)
    @LazyToOne(LazyToOneOption.NO_PROXY)
    @JsonSerialize(using = CustomQuoteSerializer.class)
    private WeeklyBasicQuote nextweek;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn( name = "WEEKTYPE", foreignKey = @ForeignKey(name = "WEEKTYPE_FK"), referencedColumnName = "TRENDVALUE")
    @JsonSerialize(using = CustomTrendSerializer.class)
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

    public WeeklyBasicQuote getPrevweek() {
        return prevweek;
    }

    public void setPrevweek(WeeklyBasicQuote prevday) {
        this.prevweek = prevday;
    }

    public WeeklyBasicQuote getNextweek() {
        return nextweek;
    }

    public void setNextweek(WeeklyBasicQuote nextday) {
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