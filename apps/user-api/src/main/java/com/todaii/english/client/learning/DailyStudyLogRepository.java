package com.todaii.english.client.learning;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.todaii.english.core.entity.learning.DailyStudyLog;

@Repository
public interface DailyStudyLogRepository extends JpaRepository<DailyStudyLog, Long> {
  Optional<DailyStudyLog> findByUserIdAndDate(Long userId, LocalDate date);
}
