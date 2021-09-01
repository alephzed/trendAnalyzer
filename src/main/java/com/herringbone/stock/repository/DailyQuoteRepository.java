package com.herringbone.stock.repository;

import com.herringbone.stock.model.DailyBasicQuote;
import com.herringbone.stock.model.DailyQuote;
import com.herringbone.stock.model.IBasicQuote;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("dailyQuote")
public interface DailyQuoteRepository extends JpaRepository<DailyQuote,Long> {

    List<DailyQuote> findByTickerIdOrderByDateDesc(Long tickerId,
                                                   Pageable page);

    @Modifying
    @Query("update DailyQuote dq set dq.nextday = ?1 where dq.id = ?2")
    void updateQuote(DailyBasicQuote dailyQuote, Long id);

    List<DailyQuote> findByTickerIdAndId(Long tickerId, Long id, Pageable page);

    @Query("SELECT g FROM DailyQuote g JOIN FETCH g.nextday JOIN FETCH g.prevday ORDER BY g.id desc")
    List<DailyQuote> findLastEager(Pageable page);

    @Query("select avg(g.volatility) from DailyBasicQuote g where g.id > (select max(gt.trendstart.id) -1  from Dailytrend gt)")
    Double getLatestVolatility();

    DailyQuote findFirstOneByTickerIdOrderByIdDesc(@Param("tickerId") Long tickerId);

    //To support last quote
//    DailyBasicQuote findTop1ByTickerIdOrderByIdDesc(Long tickerId);
    IBasicQuote findTop1ByTickerIdOrderByIdDesc(Long tickerId);

    @Query("SELECT g FROM DailyBasicQuote g WHERE g.id = (:id)")
    @QueryHints(@javax.persistence.QueryHint(name = "org.hibernate.comment", value = "Comment test"))
    DailyBasicQuote findOne(@Param("id") Long id);
}
