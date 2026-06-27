package com.todaii.english.shared.dto.learning;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StreakInfoDTO {
  Integer currentStreak;
  Integer longestStreak;
  DailyStudyLogDTO dailyStudyLog;
}
