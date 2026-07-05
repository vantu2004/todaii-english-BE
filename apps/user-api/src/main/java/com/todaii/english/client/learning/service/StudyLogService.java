package com.todaii.english.client.learning.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.todaii.english.client.learning.repository.DailyStudyLogRepository;
import com.todaii.english.client.user.UserRepository;
import com.todaii.english.core.entity.learning.DailyStudyLog;
import com.todaii.english.core.entity.user.User;
import com.todaii.english.shared.dto.learning.DailyStudyLogDTO;
import com.todaii.english.shared.dto.learning.StreakInfoDTO;
import com.todaii.english.shared.enums.StudyItemType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudyLogService {
  private final DailyStudyLogRepository dailyStudyLogRepository;
  private final UserRepository userRepository;
  private final ModelMapper modelMapper;

  public List<DailyStudyLogDTO> getAllDailyStudyLogs(Long userId) {
    List<DailyStudyLog> dailyStudyLogs = dailyStudyLogRepository.findByUserId(userId);

    return dailyStudyLogs.stream()
        .map(log -> modelMapper.map(log, DailyStudyLogDTO.class))
        .collect(Collectors.toList());
  }

  public StreakInfoDTO getStreakInfo(Long userId) {
    User user = userRepository.findById(userId).orElseThrow();

    LocalDate today = LocalDate.now();
    DailyStudyLog log = dailyStudyLogRepository.findByUserIdAndDate(userId, today).orElse(null);

    StreakInfoDTO streakInfoDTO = new StreakInfoDTO();
    streakInfoDTO.setCurrentStreak(user.getCurrentStreak());
    streakInfoDTO.setLongestStreak(user.getLongestStreak());

    // FIX TẠI ĐÂY: Nếu log khác null thì mới map, ngược lại thì gán null thẳng luôn
    DailyStudyLogDTO logDTO = (log != null) ? modelMapper.map(log, DailyStudyLogDTO.class) : null;
    streakInfoDTO.setDailyStudyLog(logDTO);

    return streakInfoDTO;
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
