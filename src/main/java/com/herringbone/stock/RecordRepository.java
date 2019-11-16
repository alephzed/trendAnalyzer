package com.herringbone.stock;

import com.herringbone.stock.model.Ticker;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecordRepository extends JpaRepository<Ticker, Long> {
}
