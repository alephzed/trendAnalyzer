package com.herringbone.herokudemo;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RecordRepository extends JpaRepository<Ticker, Long> {
}
