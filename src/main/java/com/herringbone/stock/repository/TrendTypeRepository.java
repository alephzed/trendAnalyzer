package com.herringbone.stock.repository;

import com.herringbone.stock.model.Trendtype;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrendTypeRepository extends JpaRepository<Trendtype, Long> {
    @Cacheable("trendtype")
    List<Trendtype> findByTrendvalue(long trendValue);
}
