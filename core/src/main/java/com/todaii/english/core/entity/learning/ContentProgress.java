package com.todaii.english.core.entity.learning;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.todaii.english.core.entity.user.User;
import com.todaii.english.shared.enums.TopicType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "content_progress",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uc_user_content_type",
          columnNames = {"user_id", "content_id", "content_type"})
    })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContentProgress {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(name = "content_id", nullable = false)
  private Long contentId;

  @Enumerated(EnumType.STRING)
  @Column(name = "content_type", nullable = false, length = 32)
  private TopicType contentType;

  @Column(name = "study_time", nullable = false)
  @Builder.Default
  private Integer studyTime = 0;

  @Column(name = "position", nullable = false)
  @Builder.Default
  private Float position = 0.0f;

  @Column(name = "completed", nullable = false)
  @Builder.Default
  private Boolean completed = false;

  @Column(name = "completed_at")
  private LocalDateTime completedAt;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;
}
