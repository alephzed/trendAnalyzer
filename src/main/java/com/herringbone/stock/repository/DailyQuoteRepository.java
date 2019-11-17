package com.herringbone.stock.repository;

import com.herringbone.stock.model.DailyQuote;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository("dailyQuote")
public interface DailyQuoteRepository extends JpaRepository<DailyQuote,Long> {

    List<DailyQuote> findByTickerIdOrderByDateDesc(Long tickerId,
                                                   Pageable page);

    List<DailyQuote> findByTickerIdAndId(Long tickerId, Long id, Pageable page);

//    @Query("SELECT g FROM DailyQuote g JOIN FETCH g.nextday JOIN FETCH g.prevday WHERE g.id = (:id)")
    @Query("SELECT g FROM DailyQuote g WHERE g.id = (:id)")
    DailyQuote findOne(@Param("id") Long id);

    @Query("SELECT g FROM DailyQuote g JOIN FETCH g.nextday JOIN FETCH g.prevday ORDER BY g.id desc")
    List<DailyQuote> findLastEager(Pageable page);

    @Query("select avg(g.volatility) from DailyBasicQuote g where g.id > (select max(gt.trendstart.id) -1  from Dailytrend gt)")
    Double getLatestVolatility();

    List<DailyQuote> findByDateAfter(@Param("date") ZonedDateTime dateTime);


    List<DailyQuote> findTop1ByTickerIdOrderByIdDesc(@Param("tickerId") Long tickerId);

    DailyQuote findFirstOneByTickerIdOrderByIdDesc(@Param("tickerId") Long tickerId);
}
