package com.todaii.english.shared.dto.learning;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DailyStudyLogDTO {
  private Integer totalStudyMinutes;
  private Integer testsTakenCount;
  private Integer articlesReadCount;
  private Integer videosWatchedCount;
  private Integer vocabDecksLearnedCount;
}
