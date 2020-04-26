package com.herringbone.stock.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Getter
@Setter
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
