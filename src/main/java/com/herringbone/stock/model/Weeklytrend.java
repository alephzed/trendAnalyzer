package com.herringbone.stock.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="weeklytrend")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Weeklytrend extends TrendBase<WeeklyQuote, Weeklytrend> implements java.io.Serializable, PeriodTrend {

     @Column(name="WEEKSINTRENDCOUNT")
     private Integer weeksintrendcount;

    public Integer getWeeksintrendcount() {
        return this.weeksintrendcount;
    }

    public void setWeeksintrendcount(Integer daysintrendcount) {
        this.weeksintrendcount = daysintrendcount;
    }

//    @Builder
//    public Weeklytrend(Integer weeksintrendcount, Long id, Trendtype trendtype, WeeklyQuote trendstart, WeeklyQuote trendend, Weeklytrend previoustrend, Weeklytrend nexttrend, Double trendpointchange, Double trendpercentagechange, Ticker ticker ) {
//        super(id, trendtype, trendstart, trendend, previoustrend, nexttrend, trendpointchange, trendpercentagechange, ticker, weeksintrendcount);
//        this.weeksintrendcount = weeksintrendcount;
//    }

    @Override
    public Integer getPeriodsInTrendCount() {
        return this.weeksintrendcount;
    }

    public void setPeriodsInTrendCount(Integer periodsInTrendCount) {
        this.weeksintrendcount = periodsInTrendCount;
    }
}


