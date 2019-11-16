package com.herringbone.stock.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="weeklytrend")
public class Weeklytrend extends TrendBase<WeeklyQuote, Weeklytrend> implements java.io.Serializable, PeriodTrend {

     @Column(name="WEEKSINTRENDCOUNT")
     private Integer weeksintrendcount;

    public Integer getWeeksintrendcount() {
        return this.weeksintrendcount;
    }

    public void setWeeksintrendcount(Integer daysintrendcount) {
        this.weeksintrendcount = daysintrendcount;
    }

    @Override
    public Integer getPeriodsInTrendCount() {
        return this.weeksintrendcount;
    }

    public void setPeriodsInTrendCount(Integer periodsInTrendCount) {
        this.weeksintrendcount = periodsInTrendCount;
    }
}


