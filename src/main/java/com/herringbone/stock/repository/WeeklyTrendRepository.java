package com.herringbone.stock.repository;

import com.herringbone.stock.domain.Trend;
import com.herringbone.stock.model.HistoricalTrendElement;
import com.herringbone.stock.model.Weeklytrend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Stream;

@Repository("weeklyTrend")
public interface WeeklyTrendRepository extends JpaRepository<Weeklytrend,Long> {

    String STRING = "from Weeklytrend gt1 " +
            "left join gt1.previoustrend as gt2 " +
            "left join gt1.nexttrend as gt3 " +
            "left join gt3.nexttrend as gt4 " +
            "left join gt1.trendend as g1 " +
            "left join gt2.trendend as g2 " +
            "left join gt3.trendend as g3 " +
            "left join gt4.trendend as g4 " +
            "left join gt4.nexttrend as gt5 " +
            "left join gt5.trendend as g5 " +
            "where gt2.trendtype.trendvalue = :tt1 " +
            "and gt1.trendtype.trendvalue = :tt2 " +
            "and gt3.trendtype.trendvalue = :tt3 " +
            "and g2.close / g1.close between (:point1 / :point2) - " +
            "(:precisionPct * 0.01) " +
            "and (:point1 / :point2) + (:precisionPct * 0.01) " +
            "and g3.close /g1.close between (:point3 / :point2) - " +
            "(:precisionPct * 0.01) " +
            "and (:point3 / :point2) + (:precisionPct * 0.01) " +
            "and gt3.id != :id " +
            "and gt1.ticker.id = :tickerId ";

    Weeklytrend findTop1ByTickerIdOrderByIdDesc(Long tickerId);

    @Query("Select new com.herringbone.stock.domain.Trend(gt1.id, g1end.close, g1start.close, gt2.trendstart.close, g1end.id," +
            "gt2.trendstart.id, g1start.id, g1end.date, gt1.weeksintrendcount, gt1.trendpointchange, gt1.trendpercentagechange)" +
            "from Weeklytrend gt1 left outer join gt1.previoustrend gt2 left outer join gt1.nexttrend gt3 " +
            "left outer join gt1.trendstart g1start left outer join gt1.trendend g1end "+
            "left outer join gt2.trendstart g2start left outer join gt2.trendend g2end left outer join gt3.trendend g3 " +
            "where gt1.trendtype.id = :trendId and gt1.ticker.id = :tickerId order by gt1.id desc")
    List<Trend> findMatchingTrend(@Param("trendId") Long id, @Param("tickerId"
    ) Long tickerId);

    @Query("Select new com.herringbone.stock.model.HistoricalTrendElement(gt1" +
            ".id, g2.close, g1.close, g3.close, g4.close, " +
            "g5.close, gt1.nexttrend.id, gt3.trendend.id, abs((g5.close - g4" +
            ".close) / g4.close), (g2.close / g1.close), " +
            "(g3.close / g1.close), (g4.close/g3.close), g1.id, ((g2" +
            ".volatility + g1.volatility + g3.volatility)/3), g1.date) " +
            STRING)
    Stream<HistoricalTrendElement> findSimilarTrends(@Param("tt1") long trendtype1, @Param("tt2") long trendtype2,
                                                     @Param("tt3") long trendtype3, @Param("point1") double point1,
                                                     @Param("point2") double point2, @Param("point3") double point3,
                                                     @Param("precisionPct") double precisionPct,
                                                     @Param("id") long id,
                                                     @Param("tickerId") long tickerId);

    @Query("select count(gt1) " + STRING)
    Long countSimilarTrends(@Param("tt1") long trendtype1,
                            @Param("tt2") long trendtype2,
                            @Param("tt3") long trendtype3,
                            @Param("point1") double point1,
                            @Param("point2") double point2,
                            @Param("point3") double point3,
                            @Param("precisionPct") double precisionPct,
                            @Param("id") long id,
                            @Param("tickerId") long tickerId);

    @Query("select max(g4.close/g3.close) " + STRING)
    Double maxImpulseMove(@Param("tt1") long trendtype1,
                          @Param("tt2") long trendtype2,
                          @Param("tt3") long trendtype3,
                          @Param("point1") double point1,
                          @Param("point2") double point2,
                          @Param("point3") double point3,
                          @Param("precisionPct") double precisionPct,
                          @Param("id") long id,
                          @Param("tickerId") long tickerId);

    @Query("select min(g4.close/g3.close) " + STRING)
    Double minImpulseMove(@Param("tt1") long trendtype1,
                          @Param("tt2") long trendtype2,
                          @Param("tt3") long trendtype3,
                          @Param("point1") double point1,
                          @Param("point2") double point2, @Param("point3") double point3,
                          @Param("precisionPct") double precisionPct,
                          @Param("id") long id, @Param("tickerId") long tickerId);
}
