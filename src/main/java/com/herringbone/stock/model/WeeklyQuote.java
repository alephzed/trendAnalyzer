package com.herringbone.stock.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.herringbone.stock.util.CustomQuoteSerializer;
import com.herringbone.stock.util.CustomTrendSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
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
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
public class WeeklyQuote extends QuoteBase<WeeklyBasicQuote> implements java.io.Serializable {

//    @Builder
//    public WeeklyQuote(Long id, ZonedDateTime date, Double open, Double high, Double low, Double close,
//                      Long volume, Double adjClose, Trendtype trendtype, ZonedDateTime timeentered,
//                      Double logchange, Double volatility, Double spike, Ticker ticker, Trendtype weektype,
//                      WeeklyBasicQuote prevweek, WeeklyBasicQuote nextweek) {
//        super(id, date, open, high, low, close, volume, adjClose, trendtype, timeentered,
//                logchange, volatility, spike, ticker, weektype, prevweek, nextweek);
//        this.weektype = weektype;
//        this.prevweek = prevweek;
//        this.nextweek = nextweek;
//    }

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

}
