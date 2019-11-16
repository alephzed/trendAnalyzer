package com.herringbone.stock.model;

// default package
// Generated Jan 8, 2014 8:09:55 PM by Hibernate Tools 3.4.0.CR1

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="monthlytrend")
public class Monthlytrend extends TrendBase<MonthlyQuote, Monthlytrend> implements java.io.Serializable, PeriodTrend {

     @Column(name="MONTHSINTRENDCOUNT")
     private Integer monthsintrendcount;

    public Integer getMonthsintrendcount() {
        return this.monthsintrendcount;
    }

    public void setMonthsintrendcount(Integer monthsintrendcount) {
        this.monthsintrendcount = monthsintrendcount;
    }

    @Override
    public Integer getPeriodsInTrendCount() {
        return this.monthsintrendcount;
    }

    public void setPeriodsInTrendCount(Integer periodsInTrendCount) {
        this.monthsintrendcount = periodsInTrendCount;
    }
}


