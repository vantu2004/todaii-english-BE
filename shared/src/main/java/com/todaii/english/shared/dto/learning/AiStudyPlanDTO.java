package com.todaii.english.shared.dto.learning;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiStudyPlanDTO {
  private Long id;
  private LocalDateTime createdAt;
  private List<StudyPlanTaskDTO> tasks;
  private int totalTasks;
  private int completedTasks;
}
