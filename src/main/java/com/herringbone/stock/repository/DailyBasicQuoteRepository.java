package com.herringbone.stock.repository;

import com.herringbone.stock.model.DailyBasicQuote;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("dailyBasicQuote")
public interface DailyBasicQuoteRepository extends JpaRepository<DailyBasicQuote,Long> {

    DailyBasicQuote findTop1ByTickerIdOrderByIdDesc(Long tickerId);

    List<DailyBasicQuote> findByTickerIdOrderByDateDesc(Long tickerId,
                                                        Pageable page);

    List<DailyBasicQuote> findByTickerIdAndId(Long tickerId, Long id, Pageable page);

//    @Query("SELECT g FROM DailyBasicQuote g JOIN FETCH g.nextday JOIN FETCH g.prevday WHERE g.id = (:id)")
    @Query("SELECT g FROM DailyBasicQuote g WHERE g.id = (:id)")
    DailyBasicQuote findOne(@Param("id") Long id);

    @Query("select avg(g.volatility) from DailyBasicQuote g where g.id > (select max(gt.trendstart.id) -1  from Dailytrend gt)")
    Double getLatestVolatility();

}
