package com.todaii.english.client.learning.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.todaii.english.core.entity.learning.StudyPlanTask;

@Repository
public interface StudyPlanTaskRepository extends JpaRepository<StudyPlanTask, Long> {
  Optional<StudyPlanTask> findByIdAndStudyPlanUserId(Long taskId, Long userId);
}
