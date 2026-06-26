package com.todaii.english.core.entity.learning;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import com.todaii.english.core.entity.user.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "daily_study_logs",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "date"})})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyStudyLog {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private LocalDate date;

  @Column(name = "total_study_minutes")
  @Builder.Default
  private Integer totalStudyMinutes = 0;

  @Column(name = "tests_taken_count")
  @Builder.Default
  private Integer testsTakenCount = 0;

  @Column(name = "articles_read_count")
  @Builder.Default
  private Integer articlesReadCount = 0;

  @Column(name = "videos_watched_count")
  @Builder.Default
  private Integer videosWatchedCount = 0;

  @Column(name = "vocab_decks_learned_count")
  @Builder.Default
  private Integer vocabDecksLearnedCount = 0;

  // quan hệ 1 chiều
  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;
}
