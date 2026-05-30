package com.todaii.english.core.port.redis;

import java.util.List;

import com.todaii.english.shared.response.TopWordResponse;

public interface RedisRankingPort {
  void incrementWordSearchCount(String word);

  List<TopWordResponse> getTopWords(int limit);
}
