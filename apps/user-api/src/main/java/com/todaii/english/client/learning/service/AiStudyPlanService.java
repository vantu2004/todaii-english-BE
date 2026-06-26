package com.todaii.english.client.learning.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.todaii.english.client.learning.repository.AiStudyPlanRepository;
import com.todaii.english.core.entity.learning.AiStudyPlan;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AiStudyPlanService {
  private final AiStudyPlanRepository aiStudyPlanRepository;

  public Optional<AiStudyPlan> getCurrentPlan(Long userId) {
    return aiStudyPlanRepository.findTopByUserIdOrderByCreatedAtDesc(userId);
  }

  public List<AiStudyPlan> getPlanHistory(Long userId) {
    return aiStudyPlanRepository.findByUserIdOrderByCreatedAtDesc(userId);
  }
}
