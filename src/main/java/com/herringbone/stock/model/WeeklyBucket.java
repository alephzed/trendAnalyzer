package com.herringbone.stock.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.herringbone.stock.util.CustomDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Entity
@Table(name="weeklybuckets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeeklyBucket implements Serializable {
    @EmbeddedId
    @JsonSerialize
    WeeklyBucketId weeklyBucketId;

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

