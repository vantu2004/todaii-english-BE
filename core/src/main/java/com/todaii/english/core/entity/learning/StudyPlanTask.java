package com.todaii.english.core.entity.learning;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.todaii.english.shared.enums.StudyPlanTaskType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "study_plan_tasks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudyPlanTask {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "plan_date", nullable = false)
  private LocalDate planDate;

  @Enumerated(EnumType.STRING)
  @Column(name = "task_type", nullable = false, length = 32)
  private StudyPlanTaskType taskType;

  @Column(name = "content_id", nullable = false)
  private Long contentId;

  @Column(nullable = false, length = 512)
  private String title;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Column(name = "estimated_minutes")
  private Integer estimatedMinutes;

  @Column(name = "in_progress", nullable = false)
  @Builder.Default
  private Boolean inProgress = false;

  @Column(nullable = false)
  @Builder.Default
  private Boolean completed = false;

  @Column(name = "completed_at")
  private LocalDateTime completedAt;

  @JsonIgnore
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "study_plan_id", nullable = false)
  private AiStudyPlan studyPlan;
}
