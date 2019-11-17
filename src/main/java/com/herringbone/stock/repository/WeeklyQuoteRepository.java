package com.herringbone.stock.repository;

import com.herringbone.stock.model.WeeklyQuote;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("weeklyQuote")
public interface WeeklyQuoteRepository extends JpaRepository<WeeklyQuote,Long> {

    List<WeeklyQuote> findByTickerIdOrderByDateDesc(Long tickerId,
                                                    Pageable page);

    @Query("SELECT g FROM WeeklyQuote g JOIN FETCH g.nextweek JOIN FETCH g.prevweek WHERE g.id = (:id)")
    WeeklyQuote findOneEager(@Param("id") Long id);

    @Query("SELECT g FROM WeeklyQuote g JOIN FETCH g.nextweek JOIN FETCH g.prevweek ORDER BY g.id desc")
    List<WeeklyQuote> findLastEager(Pageable page);

    @Query("select avg(g.volatility) from WeeklyQuote g where g.id > (select max(gt.trendstart.id) -1  from Weeklytrend gt)")
    Double getLatestVolatility();

    WeeklyQuote findFirstOneByTickerIdOrderByIdDesc(@Param("tickerId") Long tickerId);
}