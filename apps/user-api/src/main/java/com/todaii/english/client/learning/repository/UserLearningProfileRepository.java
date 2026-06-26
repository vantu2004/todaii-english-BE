package com.todaii.english.client.learning.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.todaii.english.core.entity.learning.UserLearningProfile;

@Repository
public interface UserLearningProfileRepository extends JpaRepository<UserLearningProfile, Long> {
  Optional<UserLearningProfile> findByUserId(Long userId);

  // Exam Countdown: lấy profile có exam_date = ngày chỉ định
  List<UserLearningProfile> findByExamDate(LocalDate examDate);

  // AI Study Plan: lấy profile có target_score > 0
  List<UserLearningProfile> findByTargetScoreGreaterThan(int minScore);
}
