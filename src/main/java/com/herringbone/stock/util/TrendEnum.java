package com.herringbone.stock.util;

import lombok.Getter;

@Getter
public enum TrendEnum {
    B1(1L),
    T1(2L),
    B2(1L),
    T2(2L),
    B(1L),
    T(2L);

    TrendEnum(Long value) {
        this.trendValue = value;
    }

    private Long trendValue;

    public static TrendEnum findByName(String name) {
        return TrendEnum.valueOf(name);
    }
}
