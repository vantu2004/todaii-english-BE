package com.todaii.english.core.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;

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
public class ToeicQuestion {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "part_number", nullable = false)
  private Integer partNumber;

  @Column(name = "question_no", nullable = false)
  private Integer questionNo;

  @Column(columnDefinition = "MEDIUMTEXT")
  private String question;

  @Column(name = "image_url", length = 1024)
  private String imageUrl;

  @Column(name = "audio_url", length = 1024)
  private String audioUrl;

  @Column(name = "option_a", columnDefinition = "MEDIUMTEXT")
  private String optionA;

  @Column(name = "option_b", columnDefinition = "MEDIUMTEXT")
  private String optionB;

  @Column(name = "option_c", columnDefinition = "MEDIUMTEXT")
  private String optionC;

  @Column(name = "option_d", columnDefinition = "MEDIUMTEXT")
  private String optionD;

  @Enumerated(EnumType.STRING)
  @Column(name = "corect_ans", nullable = false)
  private Answer correctAns;

  @Column(columnDefinition = "LONGTEXT")
  private String transcript;

  @Column(columnDefinition = "LONGTEXT")
  private String explanation;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "test_id")
  @JsonIgnore
  private ToeicTest test;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "passage_id")
  private ToeicPassage passage;

  // quan hệ 1 chiều
  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "toeic_question_tags",
      joinColumns = @JoinColumn(name = "question_id"),
      inverseJoinColumns = @JoinColumn(name = "tag_id"))
  @Builder.Default
  private Set<ToeicTag> tags = new HashSet<>();
}
