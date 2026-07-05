package com.todaii.english.client.learning.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.todaii.english.core.entity.learning.DailyStudyLog;

@Repository
public interface DailyStudyLogRepository extends JpaRepository<DailyStudyLog, Long> {
  List<DailyStudyLog> findByUserId(Long userId);

  Optional<DailyStudyLog> findByUserIdAndDate(Long userId, LocalDate date);

  List<DailyStudyLog> findByUserIdAndDateBetween(
      Long userId, LocalDate startDate, LocalDate endDate);
}
