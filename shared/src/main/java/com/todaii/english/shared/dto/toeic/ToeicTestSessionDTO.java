package com.todaii.english.shared.dto.toeic;

import java.time.LocalDateTime;
import java.util.List;

import com.todaii.english.shared.enums.ToeicSessionMode;
import com.todaii.english.shared.enums.ToeicSessionStatus;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ToeicTestSessionDTO {
  private Long id;
  private ToeicSessionMode mode;
  private ToeicSessionStatus status;
  private Integer scoreL;
  private Integer scoreR;
  private Integer totalScore;
  private Integer correctCount;
  private Integer incorrectCount;
  private Integer skippedCount;
  private Integer timeSpent;
  private String partsDone;
  private LocalDateTime startedAt;
  private LocalDateTime stoppedAt;
  private LocalDateTime completedAt;

  private Long testId;
  private List<ToeicUserAnswerDTO> userAnswers;
}
