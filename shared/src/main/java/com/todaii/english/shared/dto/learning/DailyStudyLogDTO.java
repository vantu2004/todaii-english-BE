package com.todaii.english.shared.dto.learning;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DailyStudyLogDTO {
  private LocalDate date;
  private Integer totalStudyMinutes;
  private Integer testsTakenCount;
  private Integer articlesReadCount;
  private Integer videosWatchedCount;
  private Integer vocabDecksLearnedCount;
}
