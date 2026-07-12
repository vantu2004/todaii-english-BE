package com.todaii.english.client.learning.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.todaii.english.client.learning.repository.AiStudyPlanRepository;
import com.todaii.english.client.learning.repository.StudyPlanTaskRepository;
import com.todaii.english.core.entity.learning.AiStudyPlan;
import com.todaii.english.core.entity.learning.StudyPlanTask;
import com.todaii.english.shared.dto.learning.AiStudyPlanDTO;
import com.todaii.english.shared.dto.learning.StudyPlanTaskDTO;
import com.todaii.english.shared.exceptions.BusinessException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AiStudyPlanService {
  private final AiStudyPlanRepository aiStudyPlanRepository;
  private final StudyPlanTaskRepository studyPlanTaskRepository;
  private final ModelMapper modelMapper;

  public Optional<AiStudyPlanDTO> getCurrentPlan(Long userId) {
    return aiStudyPlanRepository
        .findTopByUserIdOrderByCreatedAtDesc(userId)
        .map(this::convertToDTO);
  }

  public List<AiStudyPlanDTO> getPlanHistory(Long userId) {
    return aiStudyPlanRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
        .map(this::convertToDTO)
        .toList();
  }

  @Transactional
  public StudyPlanTaskDTO toggleTaskCompletion(Long userId, Long taskId) {
    StudyPlanTask task =
        studyPlanTaskRepository
            .findByIdAndStudyPlanUserId(taskId, userId)
            .orElseThrow(() -> new BusinessException(404, "Task not found."));

    task.setCompleted(!task.getCompleted());
    task.setCompletedAt(task.getCompleted() ? LocalDateTime.now() : null);
    StudyPlanTask saved = studyPlanTaskRepository.save(task);

    return modelMapper.map(saved, StudyPlanTaskDTO.class);
  }

  public AiStudyPlanDTO convertToDTO(AiStudyPlan plan) {
    if (plan == null) return null;

    List<StudyPlanTaskDTO> taskDTOs = new ArrayList<>();
    int completedCount = 0;
    if (plan.getTasks() != null) {
      for (StudyPlanTask task : plan.getTasks()) {
        if (Boolean.TRUE.equals(task.getCompleted())) {
          completedCount++;
        }
        taskDTOs.add(modelMapper.map(task, StudyPlanTaskDTO.class));
      }
    }

    return AiStudyPlanDTO.builder()
        .id(plan.getId())
        .createdAt(plan.getCreatedAt())
        .tasks(taskDTOs)
        .totalTasks(taskDTOs.size())
        .completedTasks(completedCount)
        .build();
  }
}
