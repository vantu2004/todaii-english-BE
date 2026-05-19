package com.todaii.english.core.repository;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.todaii.english.core.entity.UsageStatistic;
import com.todaii.english.shared.enums.ActorType;
import com.todaii.english.shared.enums.UsageType;

@Repository
public interface UsageStatisticRepository extends JpaRepository<UsageStatistic, Long> {
  UsageStatistic findByActorIdAndActorTypeAndUsageTypeAndModelAndCreatedAt(
      Long actorId, ActorType actorType, UsageType usageType, String model, LocalDate currentDate);

  UsageStatistic findByActorIdAndActorTypeAndUsageTypeAndCreatedAt(
      Long actorId, ActorType actorType, UsageType usageType, LocalDate currentDate);
}
