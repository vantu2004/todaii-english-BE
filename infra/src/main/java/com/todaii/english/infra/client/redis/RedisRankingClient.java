package com.todaii.english.infra.client.redis;

import java.util.List;

import org.springframework.stereotype.Component;

import com.todaii.english.core.port.redis.RedisRankingPort;
import com.todaii.english.shared.response.TopWordResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisRankingClient implements RedisRankingPort {
  private final JedisPool jedisPool;

  private static final String WORD_RANKING_KEY = "dictionary:top_words";

  @Override
  public void incrementWordSearchCount(String word) {
    try (Jedis jedis = jedisPool.getResource()) {
      double newScore = jedis.zincrby(WORD_RANKING_KEY, 1, word);

      log.info("Word ranking updated. word={}, totalSearches={}", word, newScore);
    }
  }

  @Override
  public List<TopWordResponse> getTopWords(int limit) {
    try (Jedis jedis = jedisPool.getResource()) {
      List<TopWordResponse> result =
          jedis.zrevrangeWithScores(WORD_RANKING_KEY, 0, limit - 1).stream()
              .map(tuple -> new TopWordResponse(tuple.getElement(), (long) tuple.getScore()))
              .toList();

      log.info("Retrieved top {} words. Found {} records", limit, result.size());

      return result;
    }
  }
}
