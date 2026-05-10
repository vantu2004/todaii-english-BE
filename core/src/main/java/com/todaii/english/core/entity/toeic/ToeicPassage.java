package com.todaii.english.core.entity.toeic;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.*;

@Entity
@Table(name = "toeic_passages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ToeicPassage extends MediaUrl {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "part_number", nullable = false)
  private Integer partNumber;

  @Column(name = "passage_text", columnDefinition = "LONGTEXT")
  private String passageText;

  @Column(name = "passage_trans", columnDefinition = "LONGTEXT")
  private String passageTrans;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

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
  private List<ToeicQuestion> questions = new ArrayList<>();
}
