package com.herringbone.stock.repository;

import com.herringbone.stock.model.WeeklyBasicQuote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository("weeklyBasicQuote")
public interface WeeklyBasicQuoteRepository extends JpaRepository<WeeklyBasicQuote,Long> {

    @Query("SELECT g FROM WeeklyBasicQuote g WHERE g.id = (:id)")
    WeeklyBasicQuote findOne(@Param("id") Long id);

    @Query("select avg(g.volatility) from WeeklyBasicQuote g where g.id > (select max(gt.trendstart.id) -1  from Weeklytrend gt)")
    Double getLatestVolatility();

}
