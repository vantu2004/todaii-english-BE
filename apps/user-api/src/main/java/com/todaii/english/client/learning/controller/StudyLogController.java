package com.todaii.english.client.learning.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.todaii.english.client.UserUtils;
import com.todaii.english.client.learning.service.StudyLogService;
import com.todaii.english.shared.dto.learning.DailyStudyLogDTO;
import com.todaii.english.shared.dto.learning.StreakInfoDTO;
import com.todaii.english.shared.enums.StudyItemType;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/study-logs")
@RequiredArgsConstructor
public class StudyLogController {
  private final StudyLogService studyLogService;

  @GetMapping
  public ResponseEntity<List<DailyStudyLogDTO>> getAllDailyStudyLogs(
      Authentication authentication) {
    Long userId = UserUtils.getCurrentUserId(authentication);

    return ResponseEntity.ok(studyLogService.getAllDailyStudyLogs(userId));
  }

  @GetMapping("/streak-info")
  public ResponseEntity<StreakInfoDTO> getStreakInfo(Authentication authentication) {
    Long userId = UserUtils.getCurrentUserId(authentication);

    return ResponseEntity.ok(studyLogService.getStreakInfo(userId));
  }

  @PostMapping("/ping")
  public ResponseEntity<Void> pingStudyTime(Authentication authentication) {
    Long userId = UserUtils.getCurrentUserId(authentication);
    studyLogService.pingStudyTime(userId);

    return ResponseEntity.ok().build();
  }

  @PostMapping("/increment")
  public ResponseEntity<Void> incrementStudyItem(
      Authentication authentication, @RequestParam StudyItemType type) {
    Long userId = UserUtils.getCurrentUserId(authentication);
    studyLogService.incrementStudyItem(userId, type);

    return ResponseEntity.ok().build();
  }
}
