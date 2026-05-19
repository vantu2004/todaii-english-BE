package com.todaii.english.core.service;

import com.todaii.english.core.repository.UsageStatisticRepository;
import com.todaii.english.shared.enums.ActorType;
import org.springframework.stereotype.Service;

import com.todaii.english.core.entity.UsageStatistic;
import com.todaii.english.core.port.UsageStatisticPort;
import com.todaii.english.shared.enums.UsageType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsageStatisticService implements UsageStatisticPort {
  private final UsageStatisticRepository usageStatisticRepository;

  @Override
  public UsageStatistic createCloudinaryStatistic(Long actorId, ActorType actorType) {
    return UsageStatistic.builder().actorId(actorId).actorType(actorType).usageType(UsageType.CLOUDINARY_UPLOAD).build();
  }

  @Override
  public UsageStatistic createUsageStatistic(UsageStatistic newStatistic) {
    UsageStatistic currStatistic;

    if (useModelKey(newStatistic.getUsageType())) {

      currStatistic =
          usageStatisticRepository.findByActorIdAndActorTypeAndUsageTypeAndModelAndCreatedAt(
              newStatistic.getActorId(),
              newStatistic.getActorType(),
              newStatistic.getUsageType(),
              newStatistic.getModel(),
              newStatistic.getCreatedAt());

    } else {
      currStatistic =
          usageStatisticRepository.findByActorIdAndActorTypeAndUsageTypeAndCreatedAt(
              newStatistic.getActorId(),
              newStatistic.getActorType(),
              newStatistic.getUsageType(),
              newStatistic.getCreatedAt());
    }

    if (currStatistic == null) {
      currStatistic =
          UsageStatistic.builder()
              .actorId(newStatistic.getActorId())
              .actorType(newStatistic.getActorType())
              .usageType(newStatistic.getUsageType())
              .createdAt(newStatistic.getCreatedAt())
              .build();
    }

    // ----- COMMON -----

    currStatistic.setQuantity(safe(currStatistic.getQuantity()) + 1);

    // ----- AI STAT -----

    currStatistic.setInputToken(
        safe(currStatistic.getInputToken()) + safe(newStatistic.getInputToken()));
    currStatistic.setOutputToken(
        safe(currStatistic.getOutputToken()) + safe(newStatistic.getOutputToken()));
    currStatistic.setTotalToken(
        safe(currStatistic.getTotalToken()) + safe(newStatistic.getTotalToken()));

    if (newStatistic.getAiProvider() != null) {
      currStatistic.setAiProvider(newStatistic.getAiProvider());
    }
    if (newStatistic.getModel() != null) {
      currStatistic.setModel(newStatistic.getModel());
    }

    // ----- YOUTUBE / GOOGLE APIs -----

    currStatistic.setQuota(safe(currStatistic.getQuota()) + safe(newStatistic.getQuota()));
    currStatistic.setCharQuantity(
        safe(currStatistic.getCharQuantity()) + safe(newStatistic.getCharQuantity()));

    return usageStatisticRepository.save(currStatistic);
  }

  private Long safe(Long value) {
    return value == null ? 0L : value;
  }

  private boolean useModelKey(UsageType usageType) {
    return usageType == UsageType.AI_REQUEST;
  }
}
