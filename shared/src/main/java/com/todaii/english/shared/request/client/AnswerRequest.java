package com.todaii.english.shared.request.client;

import jakarta.validation.constraints.NotNull;

import com.todaii.english.shared.enums.Answer;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnswerRequest {
  private Answer userChoice;
  private Boolean isMarked;

  @NotNull(message = "Question ID is required")
  private Long questionId;
}
