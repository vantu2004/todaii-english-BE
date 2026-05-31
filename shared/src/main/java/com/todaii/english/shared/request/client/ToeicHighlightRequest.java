package com.todaii.english.shared.request.client;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ToeicHighlightRequest {
  @NotNull(message = "Session ID is required")
  private Long sessionId;

  private Long questionId;
  private Long groupId;

  @NotNull(message = "Start offset is required")
  private Integer start;

  @NotNull(message = "End offset is required")
  private Integer end;

  @NotBlank(message = "Highlighted text cannot be empty")
  private String text;

  private String color;
  private String note;
}
