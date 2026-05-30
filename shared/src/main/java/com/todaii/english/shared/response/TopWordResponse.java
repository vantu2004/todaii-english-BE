package com.todaii.english.shared.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TopWordResponse {
  private String word;
  private long count;
}
