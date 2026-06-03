package com.todaii.english.client.toeic_test_session;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.todaii.english.core.entity.toeic.ToeicUserAnswer;

@Repository
public interface ToeicUserAnswerRepository extends JpaRepository<ToeicUserAnswer, Long> {
  List<ToeicUserAnswer> findBySessionId(Long sessionId);
}
