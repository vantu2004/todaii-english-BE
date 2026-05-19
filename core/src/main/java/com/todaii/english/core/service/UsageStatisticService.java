package com.todaii.english.core.service;

import org.springframework.stereotype.Service;

import com.todaii.english.core.entity.UsageStatistic;
import com.todaii.english.core.port.UsageStatisticPort;
import com.todaii.english.core.repository.UsageStatisticRepository;
import com.todaii.english.shared.enums.ActorType;
import com.todaii.english.shared.enums.UsageType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsageStatisticService implements UsageStatisticPort {
  private final UsageStatisticRepository usageStatisticRepository;

  @Override
  public UsageStatistic createCloudinaryStatistic(Long actorId, ActorType actorType) {
    return getUsageStatistic(actorId, actorType, UsageType.CLOUDINARY_UPLOAD);
  }

  @Override
  public UsageStatistic createNewsApiStatistic(Long actorId) {
    return getUsageStatistic(actorId, ActorType.ADMIN, UsageType.NEWS_API_REQUEST);
  }

  @Override
  public UsageStatistic createActorLoginStat(Long actorId, ActorType actorType) {
    return getUsageStatistic(actorId, actorType, UsageType.LOGIN_REQUEST);
  }

  @Override
  public UsageStatistic createMailSendStatistic(Long actorId, ActorType actorType) {
    return getUsageStatistic(actorId, actorType, UsageType.MAIL_SEND);
  }

  // TODO: còn dính tới dictionary nên ko tiến hành
  @Override
  public UsageStatistic createDictionaryStatistic(
      Long actorId, ActorType actorType, UsageType usageType) {
    return null;
  }

  @Override
  public UsageStatistic createYoutubeStatistic(Long actorId) {
    UsageStatistic usageStatistic =
        getUsageStatistic(actorId, ActorType.ADMIN, UsageType.YOUTUBE_SEARCH);

    // mặc định youtube data api v3 tính 1 lần search videos hay playlists là 100units
    usageStatistic.setQuota(100L);

    return usageStatistic;
  }

  @Override
  public UsageStatistic createGgTranslateStat(Long actorId, Long charQuantity) {
    actorId = actorId == null ? 0L : actorId;
    ActorType actorType = actorId == 0L ? ActorType.GUEST : ActorType.USER;

    UsageStatistic usageStatistic =
        getUsageStatistic(actorId, actorType, UsageType.GOOGLE_TRANSLATE_REQUEST);
    usageStatistic.setCharQuantity(charQuantity);

    return usageStatistic;
  }

  private UsageStatistic getUsageStatistic(Long actorId, ActorType actorType, UsageType usageType) {
    return UsageStatistic.builder()
        .actorId(actorId)
        .actorType(actorType)
        .usageType(usageType)
        .build();
  }

  // phòng trường hợp FE gửi 2 request cùng lúc do strictmode
  @Override
  public synchronized void createUsageStatistic(UsageStatistic newStatistic) {
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

    usageStatisticRepository.save(currStatistic);
  }

  private Long safe(Long value) {
    return value == null ? 0L : value;
  }

  private boolean useModelKey(UsageType usageType) {
    return usageType == UsageType.AI_REQUEST;
  }
}
