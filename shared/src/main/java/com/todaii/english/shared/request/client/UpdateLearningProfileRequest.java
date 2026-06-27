package com.todaii.english.shared.request.client;

import java.time.LocalDate;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

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
public class UpdateLearningProfileRequest {
  @Min(value = 10, message = "Target score must be at least 10")
  @Max(value = 990, message = "Target score cannot exceed 990")
  private Integer targetScore;

  private LocalDate examDate;
}
