package com.herringbone.stock.repository;

import com.herringbone.stock.model.DailyBucket;
import com.herringbone.stock.model.DailyBucketId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyBucketRepository extends JpaRepository<DailyBucket, DailyBucketId> {

}
