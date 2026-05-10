package com.todaii.english.core.entity.toeic;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.todaii.english.shared.enums.Answer;

import lombok.*;

@Entity
@Table(name = "toeic_questions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ToeicQuestion extends MediaUrl {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // auto fill by API
  @Column(name = "part_number", nullable = false)
  private Integer partNumber;

  @Column(columnDefinition = "MEDIUMTEXT")
  private String question;

  @Column(name = "option_a", columnDefinition = "MEDIUMTEXT")
  @Builder.Default
  private String optionA = "A. ";

  @Column(name = "option_b", columnDefinition = "MEDIUMTEXT")
  @Builder.Default
  private String optionB = "B. ";

  @Column(name = "option_c", columnDefinition = "MEDIUMTEXT")
  @Builder.Default
  private String optionC = "C. ";

  @Column(name = "option_d", columnDefinition = "MEDIUMTEXT")
  @Builder.Default
  private String optionD = "D. ";

  @Enumerated(EnumType.STRING)
  @Column(name = "correct_ans", nullable = false)
  private Answer correctAns;

  @Column(columnDefinition = "LONGTEXT")
  private String transcript;

  @Column(columnDefinition = "LONGTEXT")
  private String explanation;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "test_id")
  @JsonIgnore
  private ToeicTest test;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "passage_id")
  @JsonIgnore
  private ToeicPassage passage;

  // quan hệ 1 chiều
  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "toeic_question_tags",
      joinColumns = @JoinColumn(name = "question_id"),
      inverseJoinColumns = @JoinColumn(name = "tag_id"))
  @Builder.Default
  private Set<ToeicTag> tags = new HashSet<>();
}
