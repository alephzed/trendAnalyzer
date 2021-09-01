package com.herringbone.stock.repository;

import com.herringbone.stock.model.MonthlyBasicQuote;
import com.herringbone.stock.model.MonthlyQuote;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("monthlyQuote")
public interface MonthlyQuoteRepository extends JpaRepository<MonthlyQuote,Long> {

    List<MonthlyQuote> findByTickerIdOrderByDateDesc(Long tickerId,
                                                     Pageable page);

    @Query("select avg(g.volatility) from MonthlyQuote g where g.id > (select max(gt.trendstart.id) -1  from Monthlytrend gt)")
    Double getLatestVolatility();

    MonthlyQuote findFirstOneByTickerIdOrderByIdDesc(@Param("tickerId") Long tickerId);

    @Query("SELECT g FROM MonthlyBasicQuote g WHERE g.id = (:id)")
    MonthlyBasicQuote findOne(@Param("id") Long id);

    @Modifying
    @Query("update MonthlyQuote dq set dq.nextmonth = ?1 where dq.id = ?2")
    void updateQuote(MonthlyBasicQuote monthlyQuote, Long id);
}
