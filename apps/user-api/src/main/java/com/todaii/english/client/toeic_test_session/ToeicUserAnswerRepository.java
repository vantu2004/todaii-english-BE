package com.todaii.english.client.toeic_test_session;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.todaii.english.core.entity.toeic.ToeicUserAnswer;

@Repository
public interface ToeicUserAnswerRepository extends JpaRepository<ToeicUserAnswer, Long> {
  List<ToeicUserAnswer> findBySessionId(Long sessionId);

  // Trả về [partNumber, totalCount, correctCount] cho mỗi Part của user (chỉ session COMPLETED)
  @Query(
      """
      SELECT q.partNumber, COUNT(ua.id), SUM(CASE WHEN ua.status = 1 THEN 1 ELSE 0 END)
      FROM ToeicUserAnswer ua
      JOIN ua.question q
      WHERE ua.session.user.id = :userId
        AND ua.session.status = com.todaii.english.shared.enums.ToeicSessionStatus.COMPLETED
      GROUP BY q.partNumber
      ORDER BY q.partNumber
      """)
  List<Object[]> getAccuracyByPart(@Param("userId") Long userId);
}
