package com.todaii.english.client.learning.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.todaii.english.client.UserUtils;
import com.todaii.english.client.learning.service.AnalyticsService;
import com.todaii.english.shared.dto.learning.PartAccuracyDTO;
import com.todaii.english.shared.dto.learning.ScorePredictionDTO;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
public class AnalyticsController {
  private final AnalyticsService analyticsService;

  @GetMapping("/weakness")
  public ResponseEntity<List<PartAccuracyDTO>> getWeakness(Authentication authentication) {
    Long userId = UserUtils.getCurrentUserId(authentication);

    return ResponseEntity.ok(analyticsService.getWeaknessAnalysis(userId));
  }

  @GetMapping("/score-prediction")
  public ResponseEntity<ScorePredictionDTO> getScorePrediction(Authentication authentication) {
    Long userId = UserUtils.getCurrentUserId(authentication);

    return ResponseEntity.ok(analyticsService.getScorePrediction(userId));
  }
}
