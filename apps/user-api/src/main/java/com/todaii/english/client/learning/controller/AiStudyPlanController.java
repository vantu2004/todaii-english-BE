package com.todaii.english.client.learning.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.todaii.english.client.UserUtils;
import com.todaii.english.client.learning.service.AiStudyPlanService;
import com.todaii.english.shared.dto.learning.AiStudyPlanDTO;
import com.todaii.english.shared.dto.learning.StudyPlanTaskDTO;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/study-plan")
@RequiredArgsConstructor
public class AiStudyPlanController {
  private final AiStudyPlanService aiStudyPlanService;

  @GetMapping("/current")
  public ResponseEntity<AiStudyPlanDTO> getCurrentPlan(Authentication authentication) {
    Long userId = UserUtils.getCurrentUserId(authentication);

    return aiStudyPlanService
        .getCurrentPlan(userId)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.noContent().build());
  }

  @GetMapping("/history")
  public ResponseEntity<List<AiStudyPlanDTO>> getPlanHistory(Authentication authentication) {
    Long userId = UserUtils.getCurrentUserId(authentication);
    List<AiStudyPlanDTO> history = aiStudyPlanService.getPlanHistory(userId);

    return ResponseEntity.ok(history);
  }

  @PatchMapping("/tasks/{taskId}/toggle")
  public ResponseEntity<StudyPlanTaskDTO> toggleTask(
      @PathVariable Long taskId, Authentication authentication) {
    Long userId = UserUtils.getCurrentUserId(authentication);
    StudyPlanTaskDTO task = aiStudyPlanService.toggleTaskCompletion(userId, taskId);

    return ResponseEntity.ok(task);
  }
}
