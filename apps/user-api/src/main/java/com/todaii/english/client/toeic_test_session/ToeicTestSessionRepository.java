package com.todaii.english.client.toeic_test_session;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.todaii.english.core.entity.toeic.ToeicTestSession;
import com.todaii.english.shared.enums.ToeicSessionMode;
import com.todaii.english.shared.enums.ToeicSessionStatus;

@Repository
public interface ToeicTestSessionRepository extends JpaRepository<ToeicTestSession, Long> {
  List<ToeicTestSession> findByUserId(Long userId);

  Optional<ToeicTestSession> findByIdAndUserId(Long id, Long userId);

  // Lấy top N session COMPLETED gần nhất theo mode (FULL_TEST hoặc PRACTICE)
  List<ToeicTestSession> findTop3ByUserIdAndStatusAndModeOrderByCompletedAtDesc(
      Long userId, ToeicSessionStatus status, ToeicSessionMode mode);
}
