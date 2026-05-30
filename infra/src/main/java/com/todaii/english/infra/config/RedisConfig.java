package com.todaii.english.infra.config;

import java.net.URI;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Configuration
public class RedisConfig {
  @Value("${upstash.redis-url}")
  private String redisUrl;

  @Bean
  public JedisPool jedisPool() {
    GenericObjectPoolConfig<Jedis> poolConfig = new GenericObjectPoolConfig<>();
    poolConfig.setJmxEnabled(false);

    return new JedisPool(poolConfig, URI.create(redisUrl));
  }
}
