package com.herringbone.stock.service;

import com.herringbone.stock.model.DailyBucket;
import com.herringbone.stock.model.DailyBucketId;
import com.herringbone.stock.model.Dailytrend;
import com.herringbone.stock.model.MonthlyBucket;
import com.herringbone.stock.model.MonthlyBucketId;
import com.herringbone.stock.model.Monthlytrend;
import com.herringbone.stock.model.Ticker;
import com.herringbone.stock.model.WeeklyBucket;
import com.herringbone.stock.model.WeeklyBucketId;
import com.herringbone.stock.model.Weeklytrend;
import com.herringbone.stock.repository.DailyBucketRepository;
import com.herringbone.stock.repository.DailyTrendRepository;
import com.herringbone.stock.repository.MonthlyBucketRepository;
import com.herringbone.stock.repository.MonthlyTrendRepository;
import com.herringbone.stock.repository.WeeklyBucketRepository;
import com.herringbone.stock.repository.WeeklyTrendRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.TreeMap;

@Service("DailyBucketService")
public class BucketService {

    private final DailyBucketRepository dailyBucketRepository;
    private final WeeklyBucketRepository weeklyBucketRepository;
    private final MonthlyBucketRepository monthlyBucketRepository;
    private final DailyTrendRepository dailyTrendRepository;
    private final WeeklyTrendRepository weeklyTrendRepository;
    private final MonthlyTrendRepository monthlyTrendRepository;
    private final TickerService tickerService;

    public BucketService(DailyBucketRepository dailyBucketRepository, WeeklyBucketRepository weeklyBucketRepository,
                         MonthlyBucketRepository monthlyBucketRepository, DailyTrendRepository dailyTrendRepository,
                         WeeklyTrendRepository weeklyTrendRepository, MonthlyTrendRepository monthlyTrendRepository,
                         TickerService tickerService) {
        this.dailyBucketRepository = dailyBucketRepository;
        this.weeklyBucketRepository = weeklyBucketRepository;
        this.monthlyBucketRepository = monthlyBucketRepository;
        this.dailyTrendRepository = dailyTrendRepository;
        this.weeklyTrendRepository = weeklyTrendRepository;
        this.monthlyTrendRepository = monthlyTrendRepository;
        this.tickerService = tickerService;
    }

    @Transactional
    public Map getCurrentBuckets(String symbol) {
        Map<String, Object> trends = new TreeMap<>();
        Ticker ticker = tickerService.getTicker(symbol);
        //get latest uptrend and downtrend for daily, weekly, and monthly quotes
        Dailytrend latestDailyTrend = dailyTrendRepository.findTop1ByTickerIdOrderByIdDesc(ticker.getId());
        long dailyTrendType = latestDailyTrend.getTrendtype().getTrendvalue();
        DailyBucketId dailyBucketId = DailyBucketId.builder().daysInTrendCount(latestDailyTrend.getDaysintrendcount()).trendType((int) dailyTrendType).tickerid(ticker.getId()).build();
        DailyBucket dailyBucket = dailyBucketRepository.findById(dailyBucketId).orElse(DailyBucket.builder().build());
        dailyBucket.setLastStoredQuoteClose(latestDailyTrend.getTrendend().getClose());
        dailyBucket.setLastStoredQuoteDate(latestDailyTrend.getTrendend().getDate());
        trends.put("Daily", dailyBucket);

        Weeklytrend latestWeeklytrend = weeklyTrendRepository.findTop1ByTickerIdOrderByIdDesc(ticker.getId());
        long weeklyTrendType = latestWeeklytrend.getTrendtype().getTrendvalue();
        WeeklyBucketId weeklyBucketId = WeeklyBucketId.builder().weeksInTrendCount(latestWeeklytrend.getWeeksintrendcount()).tickerId(ticker.getId()).trendType((int) weeklyTrendType).build();
        WeeklyBucket weeklyBucket = weeklyBucketRepository.findById(weeklyBucketId).orElse(WeeklyBucket.builder().build());
        weeklyBucket.setLastStoredQuoteClose(latestWeeklytrend.getTrendend().getClose());
        weeklyBucket.setLastStoredQuoteDate(latestWeeklytrend.getTrendend().getDate());
        trends.put("Weekly", weeklyBucket);

        Monthlytrend latestMonthlytrend = monthlyTrendRepository.findTop1ByTickerIdOrderByIdDesc(ticker.getId());
        long monthlyTrendType = latestMonthlytrend.getTrendtype().getTrendvalue();
        MonthlyBucketId monthlyBucketId = MonthlyBucketId.builder().tickerId(ticker.getId()).monthsInTrendCount(latestMonthlytrend.getMonthsintrendcount()).trendType((int) monthlyTrendType).build();
        MonthlyBucket monthlyBucket = monthlyBucketRepository.findById(monthlyBucketId).orElse(MonthlyBucket.builder().build());
        monthlyBucket.setLastStoredQuoteClose(latestMonthlytrend.getTrendend().getClose());
        monthlyBucket.setLastStoredQuoteDate(latestMonthlytrend.getTrendend().getDate());
        trends.put("Monthly", monthlyBucket);
        return trends;
    }
}
