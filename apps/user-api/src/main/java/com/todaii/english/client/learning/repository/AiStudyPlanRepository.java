package com.todaii.english.client.learning.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.todaii.english.core.entity.learning.AiStudyPlan;

@Repository
public interface AiStudyPlanRepository extends JpaRepository<AiStudyPlan, Long> {
  // Lấy plan mới nhất của user
  Optional<AiStudyPlan> findTopByUserIdOrderByCreatedAtDesc(Long userId);

  // Lấy tất cả plan của user (lịch sử)
  List<AiStudyPlan> findByUserIdOrderByCreatedAtDesc(Long userId);
}
