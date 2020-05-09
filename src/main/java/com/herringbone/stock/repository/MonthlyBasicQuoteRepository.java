package com.herringbone.stock.repository;

import com.herringbone.stock.model.MonthlyBasicQuote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository("monthlyBasicQuote")
public interface MonthlyBasicQuoteRepository extends JpaRepository<MonthlyBasicQuote,Long> {

    @Query("SELECT g FROM MonthlyBasicQuote g WHERE g.id = (:id)")
    MonthlyBasicQuote findOne(@Param("id") Long id);

}
