package com.herringbone.stock.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.herringbone.stock.util.CustomQuoteSerializer;
import com.herringbone.stock.util.CustomTrendSerializer;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "dailyquote")
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, creatorVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class DailyQuote extends QuoteBase<DailyBasicQuote> implements java.io.Serializable {

    @OneToOne(cascade= CascadeType.ALL,fetch = FetchType.EAGER)
    @JoinColumn(name = "PREVDAY")
    @JsonSerialize(using = CustomQuoteSerializer.class)
    private DailyBasicQuote prevday;

    @OneToOne
    @JoinColumn(name = "NEXTDAY", unique = true)
    @JsonSerialize(using = CustomQuoteSerializer.class)
    private DailyBasicQuote nextday;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn( name = "DAYTYPE", foreignKey = @ForeignKey(name = "DAYTYPE_FK"), referencedColumnName = "TRENDVALUE")
    @JsonSerialize(using = CustomTrendSerializer.class)
    private Trendtype daytype;

    private static final long serialVersionUID = -3994007977478642037L;

    public Trendtype getDaytype() {
        return this.daytype;
    }

    public void setDaytype(Trendtype daytype) {
        this.daytype = daytype;
    }

    public DailyBasicQuote getPrevday() {
        return prevday;
    }

    public void setPrevday(DailyBasicQuote prevday) {
        this.prevday = prevday;
    }

    public DailyBasicQuote getNextday() {
        return nextday;
    }

    public void setNextday(DailyBasicQuote nextday) {
        this.nextday = nextday;
    }

    @Override
    public Trendtype getPeriodType() {
        return daytype;
    }

    @Override
    public void setPeriodType(Trendtype periodType) {
        this.daytype = periodType;
    }

    @Override
    public DailyBasicQuote getPrevPeriod() {
        return prevday;
    }

    @Override
    public void setPrevPeriod(DailyBasicQuote dailyBasicQuote) {
        this.prevday = dailyBasicQuote;
    }

    @Override
    public DailyBasicQuote getNextPeriod() {
        return nextday;
    }

    @Override
    public void setNextPeriod(DailyBasicQuote dailyBasicQuote) {
        this.nextday = dailyBasicQuote;
    }
}