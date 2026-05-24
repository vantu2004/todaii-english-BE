package com.todaii.english.client.toeic_test_session;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.todaii.english.core.entity.toeic.ToeicTestSession;

@Repository
public interface ToeicTestSessionRepository extends JpaRepository<ToeicTestSession, Long> {
  Page<ToeicTestSession> findByUserId(Long userId, Pageable pageable);

  List<ToeicTestSession> findByUserIdOrderByStartedAtDesc(Long userId);

  Optional<ToeicTestSession> findByIdAndUserId(Long id, Long userId);
}
