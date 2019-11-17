package com.herringbone.stock.repository;

import com.herringbone.stock.model.DailyBasicQuote;
import com.herringbone.stock.model.WeeklyBasicQuote;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("weeklyBasicQuote")
public interface WeeklyBasicQuoteRepository extends JpaRepository<WeeklyBasicQuote,Long> {

    List<DailyBasicQuote> findByTickerIdOrderByDateDesc(Long tickerId,
                                                        Pageable page);

    List<DailyBasicQuote> findByTickerIdAndId(Long tickerId, Long id, Pageable page);

    @Query("SELECT g FROM WeeklyBasicQuote g WHERE g.id = (:id)")
    WeeklyBasicQuote findOne(@Param("id") Long id);

    @Query("select avg(g.volatility) from WeeklyBasicQuote g where g.id > (select max(gt.trendstart.id) -1  from Weeklytrend gt)")
    Double getLatestVolatility();

}
