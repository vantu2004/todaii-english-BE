package com.todaii.english.shared.dto.learning;

import java.time.LocalDate;

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
public class UserLearningProfileDTO {
  private Long id;
  private Integer targetScore;
  private Integer currentScore;
  private LocalDate examDate;
}
