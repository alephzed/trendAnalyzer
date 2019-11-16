package com.herringbone.stock.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.herringbone.stock.util.CustomDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.time.ZonedDateTime;

@Entity
@Table(name="monthlybuckets")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlyBucket {
    @EmbeddedId
    @JsonSerialize
    MonthlyBucketId monthlyBucketId;

    @Column(name="cumulativepercentage")
    private double cumulativePercentage;

    @Column(name="cumulativefrequency")
    private double cumulativefrequency;

    @Column(name="frequency")
    private double frequency;

    @Column(name="percentage")
    private double percentage;

    @Transient
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    private ZonedDateTime lastStoredQuoteDate;

    @Transient
    @JsonSerialize
    private Double lastStoredQuoteClose;
}

