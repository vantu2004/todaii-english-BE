package com.todaii.english.core.port;

import com.todaii.english.core.entity.UsageStatistic;
import com.todaii.english.shared.enums.ActorType;

public interface UsageStatisticPort {
  UsageStatistic createCloudinaryStatistic(Long actorId, ActorType actorType);

  UsageStatistic createNewsApiStatistic(Long actorId, ActorType actorType);

  UsageStatistic createActorLoginStat(Long actorId, ActorType actorType);

  UsageStatistic createMailSendStatistic(Long actorId, ActorType actorType);

  void createUsageStatistic(UsageStatistic newUsageStatistic);
}
