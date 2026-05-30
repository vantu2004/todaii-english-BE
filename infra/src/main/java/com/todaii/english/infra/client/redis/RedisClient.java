package com.todaii.english.infra.client.redis;

import java.time.Duration;

import org.springframework.stereotype.Component;

import com.todaii.english.core.port.redis.RedisPort;
import com.todaii.english.shared.enums.RedisType;

import lombok.RequiredArgsConstructor;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Component
@RequiredArgsConstructor
public class RedisClient implements RedisPort {
  private final JedisPool jedisPool;
  private final Duration REFRESH_THRESHOLD = Duration.ofHours(12);

  @Override
  public void set(RedisType redisType, String rawKey, String value) {
    try (Jedis jedis = jedisPool.getResource()) {
      jedis.setex(redisType.getPrefixKey() + rawKey, redisType.getTtl().getSeconds(), value);
    }
  }

  @Override
  public String get(RedisType redisType, String rawKey) {
    try (Jedis jedis = jedisPool.getResource()) {
      return jedis.get(redisType.getPrefixKey() + rawKey);
    }
  }

  @Override
  public void refreshTtlIfNeeded(RedisType redisType, String rawKey) {
    try (Jedis jedis = jedisPool.getResource()) {
      String key = redisType.getPrefixKey() + rawKey;

      long ttl = jedis.ttl(key);

      if (ttl < REFRESH_THRESHOLD.getSeconds()) {
        jedis.expire(key, redisType.getTtl().getSeconds());
      }
    }
  }
}
