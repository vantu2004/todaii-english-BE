package com.todaii.english.core.port;

import com.todaii.english.shared.enums.RedisType;

public interface RedisPort {
  void set(RedisType redisType, String rawKey, String value);

  String get(RedisType redisType, String rawKey);

  void refreshTtlIfNeeded(RedisType redisType, String rawKey);
}
