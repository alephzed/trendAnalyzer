package com.herringbone.stock.repository;

import com.herringbone.stock.model.MonthlyQuote;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("monthlyQuote")
public interface MonthlyQuoteRepository extends JpaRepository<MonthlyQuote,Long> {

    List<MonthlyQuote> findByTickerIdOrderByDateDesc(Long tickerId,
                                                     Pageable page);

    @Query("SELECT g FROM MonthlyQuote g JOIN FETCH g.nextmonth JOIN FETCH g.prevmonth WHERE g.id = (:id)")
    MonthlyQuote findOneEager(@Param("id") Long id);

    @Query("SELECT g FROM MonthlyQuote g JOIN FETCH g.nextmonth JOIN FETCH g.prevmonth ORDER BY g.id desc")
    List<MonthlyQuote> findLastEager(Pageable page);

    @Query("select avg(g.volatility) from MonthlyQuote g where g.id > (select max(gt.trendstart.id) -1  from Monthlytrend gt)")
    Double getLatestVolatility();

    MonthlyQuote findFirstOneByTickerIdOrderByIdDesc(@Param("tickerId") Long tickerId);
}
