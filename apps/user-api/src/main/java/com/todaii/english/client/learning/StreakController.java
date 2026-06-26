package com.todaii.english.client.learning;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.todaii.english.client.UserUtils;
import com.todaii.english.client.user.UserRepository;
import com.todaii.english.core.entity.learning.DailyStudyLog;
import com.todaii.english.core.entity.user.User;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/streak")
@RequiredArgsConstructor
public class StreakController {
  private final UserRepository userRepository;
  private final DailyStudyLogRepository dailyStudyLogRepository;

  @GetMapping
  public ResponseEntity<Map<String, Object>> getStreakInfo(Authentication authentication) {
    Long userId = UserUtils.getCurrentUserId(authentication);
    User user = userRepository.findById(userId).orElseThrow();

    LocalDate today = LocalDate.now();
    DailyStudyLog log = dailyStudyLogRepository.findByUserIdAndDate(userId, today).orElse(null);

    Map<String, Object> response = new HashMap<>();
    response.put("currentStreak", user.getCurrentStreak() != null ? user.getCurrentStreak() : 0);
    response.put("longestStreak", user.getLongestStreak() != null ? user.getLongestStreak() : 0);
    response.put(
        "todayStudyMinutes",
        log != null && log.getTotalStudyMinutes() != null ? log.getTotalStudyMinutes() : 0);

    return ResponseEntity.ok(response);
  }
}
