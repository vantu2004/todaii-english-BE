package com.todaii.english.client.learning;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.todaii.english.client.user.UserRepository;
import com.todaii.english.core.entity.learning.DailyStudyLog;
import com.todaii.english.core.entity.user.User;
import com.todaii.english.shared.enums.StudyItemType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudyLogService {
  private final DailyStudyLogRepository dailyStudyLogRepository;
  private final UserRepository userRepository;

  public Map<String, Object> getStreakInfo(Long userId) {
    User user = userRepository.findById(userId).orElseThrow();

    LocalDate today = LocalDate.now();
    DailyStudyLog log = dailyStudyLogRepository.findByUserIdAndDate(userId, today).orElse(null);

    Map<String, Object> streakInfo = new HashMap<>();
    streakInfo.put("currentStreak", user.getCurrentStreak() != null ? user.getCurrentStreak() : 0);
    streakInfo.put("longestStreak", user.getLongestStreak() != null ? user.getLongestStreak() : 0);
    streakInfo.put(
        "todayStudyMinutes",
        log != null && log.getTotalStudyMinutes() != null ? log.getTotalStudyMinutes() : 0);

    return streakInfo;
  }

  @Transactional
  public void pingStudyTime(Long userId) {
    LocalDate today = LocalDate.now();

    DailyStudyLog log = getOrCreateDailyLog(userId, today);
    log.setTotalStudyMinutes(
        (log.getTotalStudyMinutes() != null ? log.getTotalStudyMinutes() : 0) + 5);

    dailyStudyLogRepository.save(log);

    updateUserStreak(userId, today);
  }

  @Transactional
  public void incrementStudyItem(Long userId, StudyItemType type) {
    LocalDate today = LocalDate.now();
    DailyStudyLog log = getOrCreateDailyLog(userId, today);

    switch (type) {
      case TEST:
        log.setTestsTakenCount(
            (log.getTestsTakenCount() != null ? log.getTestsTakenCount() : 0) + 1);
        break;
      case ARTICLE:
        log.setArticlesReadCount(
            (log.getArticlesReadCount() != null ? log.getArticlesReadCount() : 0) + 1);
        break;
      case VIDEO:
        log.setVideosWatchedCount(
            (log.getVideosWatchedCount() != null ? log.getVideosWatchedCount() : 0) + 1);
        break;
      case VOCAB_DECK:
        log.setVocabDecksLearnedCount(
            (log.getVocabDecksLearnedCount() != null ? log.getVocabDecksLearnedCount() : 0) + 1);
        break;
    }
    dailyStudyLogRepository.save(log);

    updateUserStreak(userId, today);
  }

  private DailyStudyLog getOrCreateDailyLog(Long userId, LocalDate date) {
    return dailyStudyLogRepository
        .findByUserIdAndDate(userId, date)
        .orElseGet(
            () -> {
              User user = userRepository.findById(userId).orElseThrow();
              return DailyStudyLog.builder()
                  .user(user)
                  .date(date)
                  .totalStudyMinutes(0)
                  .testsTakenCount(0)
                  .articlesReadCount(0)
                  .videosWatchedCount(0)
                  .vocabDecksLearnedCount(0)
                  .build();
            });
  }

  private void updateUserStreak(Long userId, LocalDate today) {
    User user = userRepository.findById(userId).orElseThrow();

    if (user.getLastStudyDate() == null || user.getLastStudyDate().isBefore(today.minusDays(1))) {
      // Either first time, or missed yesterday
      user.setCurrentStreak(1);
    } else if (user.getLastStudyDate().isEqual(today.minusDays(1))) {
      // Studied yesterday, increment
      user.setCurrentStreak((user.getCurrentStreak() != null ? user.getCurrentStreak() : 0) + 1);
    }
    // If lastStudyDate == today, do nothing (streak already incremented today)

    // Update longest streak
    if (user.getCurrentStreak() != null
        && (user.getLongestStreak() == null || user.getCurrentStreak() > user.getLongestStreak())) {
      user.setLongestStreak(user.getCurrentStreak());
    }

    user.setLastStudyDate(today);

    userRepository.save(user);
  }
}
