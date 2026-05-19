package com.todaii.english.core.port;

import com.todaii.english.core.entity.UsageStatistic;
import com.todaii.english.shared.enums.ActorType;
import com.todaii.english.shared.enums.UsageType;

public interface UsageStatisticPort {
  UsageStatistic createCloudinaryStatistic(Long actorId, ActorType actorType);

  UsageStatistic createNewsApiStatistic(Long actorId);

  UsageStatistic createActorLoginStat(Long actorId, ActorType actorType);

  UsageStatistic createMailSendStatistic(Long actorId, ActorType actorType);

  UsageStatistic createDictionaryStatistic(Long actorId, ActorType actorType, UsageType usageType);

  UsageStatistic createYoutubeStatistic(Long actorId);

  void createUsageStatistic(UsageStatistic newUsageStatistic);
}
