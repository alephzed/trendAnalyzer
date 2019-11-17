package com.herringbone.stock.service;

import com.herringbone.stock.domain.Trend;
import com.herringbone.stock.model.HistoricalTrendElement;
import com.herringbone.stock.model.Ticker;
import com.herringbone.stock.repository.WeeklyQuoteRepository;
import com.herringbone.stock.repository.WeeklyTrendRepository;
import com.herringbone.stock.util.Period;
import com.herringbone.stock.util.TrendEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

@Service(Period.FindService.WEEKLY_FIND_SERVICE)
@Slf4j
public class WeeklyStockFindService implements StockFindService {

    private final WeeklyQuoteRepository weeklyQuoteRepository;
    private final WeeklyTrendRepository weeklyTrendRepository;

    public WeeklyStockFindService(WeeklyQuoteRepository weeklyQuoteRepository, WeeklyTrendRepository weeklyTrendRepository) {
        this.weeklyQuoteRepository = weeklyQuoteRepository;
        this.weeklyTrendRepository = weeklyTrendRepository;
    }

    @Override
    public Double getLatestVolatility() {
        return weeklyQuoteRepository.getLatestVolatility();
    }

    @Override
    public Stream<HistoricalTrendElement> findHistoricalMatchingTrends(Trend trend, Integer precision, Double currentTrendVolatility, Ticker ticker) {
        return
                weeklyTrendRepository.findSimilarTrends(TrendEnum.findByName(trend.getBeginType()).getTrendValue(),
                        TrendEnum.findByName(trend.getMiddleType()).getTrendValue(),
                        TrendEnum.findByName(trend.getEndType()).getTrendValue(),
                        trend.getBeginning(), trend.getMiddle(), trend.getEnd(), precision, trend.getTrendId(), ticker.getId() );
    }

    @Override
    public Long findSimilarTrendsCount(Trend trend, Integer precision,
                                       Double currentTrendVolatility,
                                       Ticker ticker) {
        return weeklyTrendRepository.countSimilarTrends(TrendEnum.findByName(trend.getBeginType()).getTrendValue(),
                TrendEnum.findByName(trend.getMiddleType()).getTrendValue(),
                TrendEnum.findByName(trend.getEndType()).getTrendValue(),
                trend.getBeginning(), trend.getMiddle(), trend.getEnd(), precision, trend.getTrendId(), ticker.getId());
    }

    @Override
    public Double findMaxNextImpulseMove(Trend trend, Integer precision,
                                         Double currentTrendVolatility,
                                         Ticker ticker) {
        return weeklyTrendRepository.maxImpulseMove(TrendEnum.findByName(trend.getBeginType()).getTrendValue(),
                TrendEnum.findByName(trend.getMiddleType()).getTrendValue(),
                TrendEnum.findByName(trend.getEndType()).getTrendValue(),
                trend.getBeginning(), trend.getMiddle(), trend.getEnd(), precision, trend.getTrendId(), ticker.getId());
    }

    @Override
    public Double findMinNextImpulseMove(Trend trend, Integer precision, Double currentTrendVolatility, Ticker ticker) {
        return weeklyTrendRepository.minImpulseMove(TrendEnum.findByName(trend.getBeginType()).getTrendValue(),
                TrendEnum.findByName(trend.getMiddleType()).getTrendValue(),
                TrendEnum.findByName(trend.getEndType()).getTrendValue(),
                trend.getBeginning(), trend.getMiddle(), trend.getEnd(), precision, trend.getTrendId(), ticker.getId());
    }
}
