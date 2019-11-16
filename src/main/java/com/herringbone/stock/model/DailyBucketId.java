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
@NoArgsConstructor
@AllArgsConstructor
public class DailyBucketId implements Serializable {
    @Column(name="trendtype")
    int trendType;
    @Column(name="daysintrendcount")
    int daysInTrendCount;
    @Column(name="tickerid")
    long tickerid;

    @Override
    public String toString() {
        return trendType + "-" +
                daysInTrendCount + "-" +
                tickerid;
    }
}
