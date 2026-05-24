package com.todaii.english.core.entity.toeic;

import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.todaii.english.shared.enums.Answer;

import lombok.*;

@Entity
@Table(name = "toeic_user_answers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ToeicUserAnswer {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "session_id", nullable = false)
  @JsonIgnore
  private ToeicTestSession session;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "question_id", nullable = false)
  @JsonIgnore
  private ToeicQuestion question;

  @Enumerated(EnumType.STRING)
  @Column(name = "user_choice")
  private Answer userChoice;

  @Column(nullable = false)
  private Integer status; // 1: Đúng, 0: Sai, 2: Bỏ qua

  @Builder.Default
  @Column(name = "is_marked", nullable = false, columnDefinition = "bit(1) default b'0'")
  private Boolean isMarked = false;
}
