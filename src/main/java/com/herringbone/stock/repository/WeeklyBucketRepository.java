package com.herringbone.stock.repository;

import com.herringbone.stock.model.WeeklyBucket;
import com.herringbone.stock.model.WeeklyBucketId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeeklyBucketRepository extends JpaRepository<WeeklyBucket, WeeklyBucketId> {

}
