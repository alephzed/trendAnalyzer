package com.herringbone.stock.repository;

import com.herringbone.stock.model.DailyBasicQuote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository("dailyBasicQuote")
public interface DailyBasicQuoteRepository extends JpaRepository<DailyBasicQuote,Long> {

    @Query("SELECT g FROM DailyBasicQuote g WHERE g.id = (:id)")
    DailyBasicQuote findOne(@Param("id") Long id);
}
