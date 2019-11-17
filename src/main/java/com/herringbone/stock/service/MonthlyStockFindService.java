package com.herringbone.stock.service;

import com.herringbone.stock.domain.Trend;
import com.herringbone.stock.model.HistoricalTrendElement;
import com.herringbone.stock.model.Ticker;
import com.herringbone.stock.repository.MonthlyQuoteRepository;
import com.herringbone.stock.repository.MonthlyTrendRepository;
import com.herringbone.stock.util.Period;
import com.herringbone.stock.util.TrendEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

@Service(Period.FindService.MONTHLY_FIND_SERVICE)
@Slf4j
public class MonthlyStockFindService implements StockFindService {

    private final MonthlyQuoteRepository monthlyQuoteRepository;
    private final MonthlyTrendRepository monthlyTrendRepository;

    public MonthlyStockFindService(MonthlyQuoteRepository monthlyQuoteRepository, MonthlyTrendRepository monthlyTrendRepository) {
        this.monthlyQuoteRepository = monthlyQuoteRepository;
        this.monthlyTrendRepository = monthlyTrendRepository;
    }

    @Override
    public Double getLatestVolatility() {
        return monthlyQuoteRepository.getLatestVolatility();
    }

    @Override
    public Stream<HistoricalTrendElement> findHistoricalMatchingTrends(Trend trend, Integer precision, Double currentTrendVolatility, Ticker ticker) {
        return monthlyTrendRepository.findSimilarTrends(TrendEnum.findByName(trend.getBeginType()).getTrendValue(),
                        TrendEnum.findByName(trend.getMiddleType()).getTrendValue(),
                        TrendEnum.findByName(trend.getEndType()).getTrendValue(),
                        trend.getBeginning(), trend.getMiddle(), trend.getEnd(), precision, trend.getTrendId(), ticker.getId() );
    }

    @Override
    public Long findSimilarTrendsCount(Trend trend, Integer precision,
                                       Double currentTrendVolatility,
                                       Ticker ticker) {
        return monthlyTrendRepository.countSimilarTrends(TrendEnum.findByName(trend.getBeginType()).getTrendValue(),
                TrendEnum.findByName(trend.getMiddleType()).getTrendValue(),
                TrendEnum.findByName(trend.getEndType()).getTrendValue(),
                trend.getBeginning(), trend.getMiddle(), trend.getEnd(), precision, trend.getTrendId(), ticker.getId() );
    }

    @Override
    public Double findMaxNextImpulseMove(Trend trend, Integer precision,
                                         Double currentTrendVolatility,
                                         Ticker ticker) {
        return monthlyTrendRepository.maxImpulseMove(TrendEnum.findByName(trend.getBeginType()).getTrendValue(),
                TrendEnum.findByName(trend.getMiddleType()).getTrendValue(),
                TrendEnum.findByName(trend.getEndType()).getTrendValue(),
                trend.getBeginning(), trend.getMiddle(), trend.getEnd(), precision, trend.getTrendId(), ticker.getId() );
    }

    @Override
    public Double findMinNextImpulseMove(Trend trend, Integer precision, Double currentTrendVolatility, Ticker ticker) {
        return monthlyTrendRepository.minImpulseMove(TrendEnum.findByName(trend.getBeginType()).getTrendValue(),
                TrendEnum.findByName(trend.getMiddleType()).getTrendValue(),
                TrendEnum.findByName(trend.getEndType()).getTrendValue(),
                trend.getBeginning(), trend.getMiddle(), trend.getEnd(), precision, trend.getTrendId(), ticker.getId());
    }
}
