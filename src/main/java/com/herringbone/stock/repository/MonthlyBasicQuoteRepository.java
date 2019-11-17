package com.herringbone.stock.repository;

import com.herringbone.stock.model.MonthlyBasicQuote;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("monthlyBasicQuote")
public interface MonthlyBasicQuoteRepository extends JpaRepository<MonthlyBasicQuote,Long> {

    List<MonthlyBasicQuote> findByTickerIdOrderByDateDesc(Long tickerId,
                                                          Pageable page);

    List<MonthlyBasicQuote> findByTickerIdAndId(Long tickerId, Long id, Pageable page);

//    @Query("SELECT g FROM DailyBasicQuote g JOIN FETCH g.nextday JOIN FETCH g.prevday WHERE g.id = (:id)")
    @Query("SELECT g FROM MonthlyBasicQuote g WHERE g.id = (:id)")
    MonthlyBasicQuote findOne(@Param("id") Long id);

    @Query("select avg(g.volatility) from MonthlyBasicQuote g where g.id > (select max(gt.trendstart.id) -1  from Monthlytrend gt)")
    Double getLatestVolatility();

}
