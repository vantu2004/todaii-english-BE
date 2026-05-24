package com.todaii.english.shared.request.client;

import java.util.List;

import jakarta.validation.constraints.NotNull;

import com.todaii.english.shared.enums.ToeicSessionMode;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StartSessionRequest {
  @NotNull(message = "Test ID is required")
  private Long testId;

  @NotNull(message = "Session mode is required")
  private ToeicSessionMode mode;

  private List<Integer> partsDone;
}
