package com.todaii.english.shared.enums;

import java.time.Duration;

import lombok.Getter;

@Getter
public enum RedisType {
  ARTICLE("article:", Duration.ofDays(1)),
  VIDEO("video:", Duration.ofDays(1)),
  VOCAB_DECK("vocab_deck:", Duration.ofDays(1)),
  TOEIC_TEST("toeic_test:", Duration.ofDays(1)),
  DICT_WORD("dict_word:", Duration.ofDays(7));

  private final String prefixKey;
  private final Duration ttl;

  RedisType(String prefixKey, Duration ttl) {
    this.prefixKey = prefixKey;
    this.ttl = ttl;
  }
}
