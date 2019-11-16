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
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeeklyBucketId implements Serializable {
    @Column(name="trendtype")
    private
    int trendType;
    @Column(name="weeksintrendcount")
    private
    int weeksInTrendCount;
    @Column(name="tickerid")
    private
    long tickerId;

    @Override
    public String toString() {
        return trendType + "-" +
                weeksInTrendCount + "-" +
                tickerId;
    }
}
