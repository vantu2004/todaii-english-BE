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

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  @JsonIgnore
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "test_id", nullable = false)
  @JsonIgnore
  private ToeicTest test;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 32)
  private ToeicSessionMode mode;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 32)
  private ToeicSessionStatus status;

  @Builder.Default
  @Column(name = "score_l", nullable = false)
  private Integer scoreL = 0;

  @Builder.Default
  @Column(name = "score_r", nullable = false)
  private Integer scoreR = 0;

  @Builder.Default
  @Column(name = "total_score", nullable = false)
  private Integer totalScore = 0;

  @Builder.Default
  @Column(name = "correct_count", nullable = false)
  private Integer correctCount = 0;

  @Builder.Default
  @Column(name = "incorrect_count", nullable = false)
  private Integer incorrectCount = 0;

  @Builder.Default
  @Column(name = "skipped_count", nullable = false)
  private Integer skippedCount = 0;

  @Builder.Default
  @Column(name = "time_spent", nullable = false)
  private Integer timeSpent = 0;

  @Convert(converter = IntegerListConverter.class)
  @Column(name = "parts_done", columnDefinition = "json")
  private List<Integer> partsDone;

  @CreationTimestamp
  @Column(name = "started_at", nullable = false, updatable = false)
  private LocalDateTime startedAt;

  @Column(name = "completed_at")
  private LocalDateTime completedAt;

  @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  @JsonIgnore
  private List<ToeicUserAnswer> userAnswers = new ArrayList<>();
}
