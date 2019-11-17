package com.herringbone.stock.repository;

import com.herringbone.stock.model.MonthlyBucket;
import com.herringbone.stock.model.MonthlyBucketId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MonthlyBucketRepository extends JpaRepository<MonthlyBucket, MonthlyBucketId> {

}
