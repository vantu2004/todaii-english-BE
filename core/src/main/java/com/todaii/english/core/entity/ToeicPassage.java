package com.todaii.english.core.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.*;

@Entity
@Table(name = "toeic_passages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ToeicPassage {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "part_number", nullable = false)
  private Integer partNumber;

  @Column(name = "passage_text", columnDefinition = "LONGTEXT")
  private String passageText;

  @Column(name = "passage_trans", columnDefinition = "LONGTEXT")
  private String passageTrans;

  @Column(name = "image_url", length = 1024)
  private String imageUrl;

  @Column(name = "audio_url", length = 1024)
  private String audioUrl;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "test_id")
  @JsonIgnore
  private ToeicTest test;

  /*   đặt ignore chỗ này vì xuất đề thi theo thứ tự question, nếu xuất theo part thì ko rõ thứ tự, từ question truy ra passage*/
  @OneToMany(
      mappedBy = "passage",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.LAZY)
  @Builder.Default
  @JsonIgnore
  private List<ToeicQuestion> questions = new ArrayList<>();
}
