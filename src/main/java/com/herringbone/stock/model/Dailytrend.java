package com.herringbone.stock.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="dailytrend")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Dailytrend extends TrendBase<DailyQuote, Dailytrend>  implements java.io.Serializable, PeriodTrend {

    @Column(name="DAYSINTRENDCOUNT")
    private Integer daysintrendcount;

    public Integer getDaysintrendcount() {
        return daysintrendcount;
    }

    public void setDaysintrendcount(Integer daysintrendcount) {
        this.daysintrendcount = daysintrendcount;
    }

//    @Builder
//    public Dailytrend(Integer daysintrendcount, Long id, Trendtype trendtype, DailyQuote trendstart, DailyQuote trendend, Dailytrend previoustrend, Dailytrend nexttrend, Double trendpointchange, Double trendpercentagechange, Ticker ticker ) {
//        super(id, trendtype, trendstart, trendend, previoustrend, nexttrend, trendpointchange, trendpercentagechange, ticker, daysintrendcount);
//        this.daysintrendcount = daysintrendcount;
//    }

    @Override
    public Integer getPeriodsInTrendCount() {
        return this.daysintrendcount;
    }

    public void setPeriodsInTrendCount(Integer periodsInTrendCount) {
        this.daysintrendcount = periodsInTrendCount;
    }
}


