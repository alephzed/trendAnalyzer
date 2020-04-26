package com.herringbone.stock.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.herringbone.stock.util.CustomDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.ZonedDateTime;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name="ticker")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Ticker implements Serializable {
    @Id
    @GeneratedValue(strategy=IDENTITY)
    @Column(name="ID", unique=true, nullable=false)
    Long id;

    @Column(name="SYMBOL", nullable=false, length=10)
    private String symbol;

    @Column(name="ALIAS", length=10)
    private String alias;

    @Column(name="EXCHANGE")
    private String exchange;

    @Column(name="LASTUPDATE")
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    private ZonedDateTime lastUpdate;

    @Column(name = "STARTDATE", nullable = false, length = 10)
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    private
    ZonedDateTime startdate;

}
