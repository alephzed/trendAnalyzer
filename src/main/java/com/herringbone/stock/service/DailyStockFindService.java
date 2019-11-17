package com.herringbone.stock.service;

import com.herringbone.stock.domain.Trend;
import com.herringbone.stock.model.HistoricalTrendElement;
import com.herringbone.stock.model.Ticker;
import com.herringbone.stock.repository.DailyQuoteRepository;
import com.herringbone.stock.repository.DailyTrendRepository;
import com.herringbone.stock.util.Period;
import com.herringbone.stock.util.TrendEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

@Service(Period.FindService.DAILY_FIND_SERVICE)
@Slf4j
public class DailyStockFindService implements StockFindService {

    private final DailyQuoteRepository dailyQuoteRepository;
    private final DailyTrendRepository dailyTrendRepository;

    public DailyStockFindService(DailyQuoteRepository dailyQuoteRepository, DailyTrendRepository dailyTrendRepository) {
        this.dailyQuoteRepository = dailyQuoteRepository;
        this.dailyTrendRepository = dailyTrendRepository;
    }

    @Override
    public Double getLatestVolatility() {
        return dailyQuoteRepository.getLatestVolatility();
    }

    @Override
    public Stream<HistoricalTrendElement> findHistoricalMatchingTrends(Trend trend, Integer precision, Double currentTrendVolatility, Ticker ticker) {
        return dailyTrendRepository.findSimilarTrends(TrendEnum.findByName(trend.getBeginType()).getTrendValue(),
                                TrendEnum.findByName(trend.getMiddleType()).getTrendValue(),
                                TrendEnum.findByName(trend.getEndType()).getTrendValue(),
                                trend.getBeginning(), trend.getMiddle(), trend.getEnd(), precision, trend.getTrendId(), ticker.getId() );
    }

    @Override
    public Long findSimilarTrendsCount(Trend trend, Integer precision, Double currentTrendVolatility, Ticker ticker) {
        return dailyTrendRepository.countSimilarTrends(TrendEnum.findByName(trend.getBeginType()).getTrendValue(),
                TrendEnum.findByName(trend.getMiddleType()).getTrendValue(),
                TrendEnum.findByName(trend.getEndType()).getTrendValue(),
                trend.getBeginning(), trend.getMiddle(), trend.getEnd(),
                precision, trend.getTrendId(), ticker.getId());
    }

    @Override
    public Double findMaxNextImpulseMove(Trend trend, Integer precision,
                                         Double currentTrendVolatility,
                                         Ticker ticker) {
        return dailyTrendRepository.maxImpulseMove(TrendEnum.findByName(trend.getBeginType()).getTrendValue(),
                TrendEnum.findByName(trend.getMiddleType()).getTrendValue(),
                TrendEnum.findByName(trend.getEndType()).getTrendValue(),
                trend.getBeginning(), trend.getMiddle(), trend.getEnd(),
                precision, trend.getTrendId(), ticker.getId());
    }

    @Override
    public Double findMinNextImpulseMove(Trend trend, Integer precision, Double currentTrendVolatility, Ticker ticker) {
        return dailyTrendRepository.minImpulseMove(TrendEnum.findByName(trend.getBeginType()).getTrendValue(),
                TrendEnum.findByName(trend.getMiddleType()).getTrendValue(),
                TrendEnum.findByName(trend.getEndType()).getTrendValue(),
                trend.getBeginning(), trend.getMiddle(), trend.getEnd(),
                precision, trend.getTrendId(), ticker.getId());
    }
}
