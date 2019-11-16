package com.herringbone.stock.util;

import lombok.Getter;

public enum Period {
    Daily("d", FindService.DAILY_FIND_SERVICE),
    Weekly("wk", FindService.WEEKLY_FIND_SERVICE),
    Monthly("mo", FindService.MONTHLY_FIND_SERVICE);

    @Getter
    private final String abbreviation;
    @Getter
    private final String findService;

    private Period(String abbreviation, String findService) {
        this.abbreviation = abbreviation;
        this.findService = findService;
    }

    public interface FindService {
        String DAILY_FIND_SERVICE = "dailyFindService";
        String WEEKLY_FIND_SERVICE = "weeklyFindService";
        String MONTHLY_FIND_SERVICE = "monthlyFindService";
    }
}
