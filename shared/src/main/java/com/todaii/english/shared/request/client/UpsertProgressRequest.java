package com.todaii.english.shared.request.client;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import com.todaii.english.shared.enums.TopicType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpsertProgressRequest {
  @NotNull(message = "ContentType must not be null")
  private TopicType contentType;

  @NotNull(message = "StudyTimeDelta must not be null")
  @Min(value = 0, message = "StudyTimeDelta must not be negative")
  private Integer studyTimeDelta;

  @NotNull(message = "Position must not be null")
  @Min(value = 0, message = "Position must not be negative")
  private Float position;
}
