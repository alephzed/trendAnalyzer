package com.herringbone.stock.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyBucketId implements Serializable {
    @Column(name="trendtype")
    private int trendType;
    @Column(name="monthsintrendcount")
    private int monthsInTrendCount;
    @Column(name="tickerid")
    private long tickerId;

    @Override
    public String toString() {
        return trendType + "-" +
                monthsInTrendCount + "-" +
                tickerId;
    }
}
