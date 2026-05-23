package com.todaii.english.infra.config;

import java.net.URI;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import redis.clients.jedis.Jedis;

@Configuration
public class RedisConfig {
  @Bean
  public Jedis jedisPooled() {
    String UPSTASH_REDIS_URL = System.getenv("UPSTASH_REDIS_URL");

    return new Jedis(URI.create(UPSTASH_REDIS_URL));
  }
}
