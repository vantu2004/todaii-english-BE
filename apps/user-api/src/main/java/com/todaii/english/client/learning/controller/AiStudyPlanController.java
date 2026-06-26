package com.todaii.english.client.learning.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.todaii.english.client.UserUtils;
import com.todaii.english.client.learning.service.AiStudyPlanService;
import com.todaii.english.core.entity.learning.AiStudyPlan;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/study-plan")
@RequiredArgsConstructor
public class AiStudyPlanController {
  private final AiStudyPlanService aiStudyPlanService;

  @GetMapping("/current")
  public ResponseEntity<AiStudyPlan> getCurrentPlan(Authentication authentication) {
    Long userId = UserUtils.getCurrentUserId(authentication);

    return aiStudyPlanService
        .getCurrentPlan(userId)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.noContent().build());
  }

  @GetMapping("/history")
  public ResponseEntity<List<AiStudyPlan>> getPlanHistory(Authentication authentication) {
    Long userId = UserUtils.getCurrentUserId(authentication);
    List<AiStudyPlan> history = aiStudyPlanService.getPlanHistory(userId);

    return ResponseEntity.ok(history);
  }
}
