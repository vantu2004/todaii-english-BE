package com.todaii.english.core.entity.toeic;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.todaii.english.core.entity.user.User;
import com.todaii.english.shared.enums.ToeicSessionMode;
import com.todaii.english.shared.enums.ToeicSessionStatus;

import lombok.*;

@Entity
@Table(name = "toeic_test_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ToeicTestSession {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 32)
  @Builder.Default
  private ToeicSessionMode mode = ToeicSessionMode.FULL_TEST;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 32)
  private ToeicSessionStatus status;

  @Column(name = "score_l", nullable = false)
  @Builder.Default
  private Integer scoreL = 0;

  @Column(name = "score_r", nullable = false)
  @Builder.Default
  private Integer scoreR = 0;

  @Column(name = "total_score", nullable = false)
  @Builder.Default
  private Integer totalScore = 0;

  @Column(name = "correct_count", nullable = false)
  @Builder.Default
  private Integer correctCount = 0;

  @Column(name = "incorrect_count", nullable = false)
  @Builder.Default
  private Integer incorrectCount = 0;

  @Column(name = "skipped_count", nullable = false)
  @Builder.Default
  private Integer skippedCount = 0;

  // minute
  @Column(name = "time_spent", nullable = false)
  @Builder.Default
  private Integer timeSpent = 120;

  @Column(name = "parts_done", nullable = false, length = 64)
  @Builder.Default
  private String partsDone = "1, 2, 3, 4, 5, 6, 7";

  @CreationTimestamp
  @Column(name = "started_at", nullable = false, updatable = false)
  private LocalDateTime startedAt;

  @Column(name = "completed_at")
  private LocalDateTime completedAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  @JsonIgnore
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "test_id", nullable = false)
  @JsonIgnore
  private ToeicTest test;

  @OneToMany(
      mappedBy = "session",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.LAZY)
  @Builder.Default
  @JsonIgnore
  private List<ToeicUserAnswer> userAnswers = new ArrayList<>();
}
