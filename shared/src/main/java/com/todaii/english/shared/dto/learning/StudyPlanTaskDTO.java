package com.todaii.english.shared.dto.learning;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.todaii.english.shared.enums.StudyPlanTaskType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyPlanTaskDTO {
  private Long id;
  private LocalDate planDate;
  private StudyPlanTaskType taskType;
  private Long contentId;
  private String title;
  private String description;
  private Integer estimatedMinutes;
  private Boolean inProgress;
  private Boolean completed;
  private LocalDateTime completedAt;
}
