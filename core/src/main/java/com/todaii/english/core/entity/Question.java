package com.todaii.english.core.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import org.hibernate.annotations.CreationTimestamp;

import com.todaii.english.shared.enums.Answer;
import com.todaii.english.shared.enums.TopicType;

import lombok.*;

@Entity
@Table(name = "questions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Question {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(name = "topic_type", nullable = false, length = 32)
  private TopicType topicType;

  @Column(name = "content_id", nullable = false)
  private Long contentId;

  @Lob
  @Column(name = "question_text", columnDefinition = "TEXT", nullable = false)
  private String questionText;

  @Column(name = "option_a", nullable = false, length = 1024)
  private String optionA;

  @Column(name = "option_b", nullable = false, length = 1024)
  private String optionB;

  @Column(name = "option_c", nullable = false, length = 1024)
  private String optionC;

  @Column(name = "option_d", nullable = false, length = 1024)
  private String optionD;

  @Enumerated(EnumType.STRING)
  @Column(name = "correct_option", nullable = false, length = 8)
  private Answer correctOption;

  @Lob
  @Column(columnDefinition = "TEXT")
  private String explanation;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;
}
