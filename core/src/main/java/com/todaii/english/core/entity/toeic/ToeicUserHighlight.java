package com.todaii.english.core.entity.toeic;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.*;

@Entity
@Table(name = "toeic_user_highlights")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ToeicUserHighlight {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "session_id", nullable = false)
  @JsonIgnore
  private ToeicTestSession session;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "question_id")
  @JsonIgnore
  private ToeicQuestion question;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "group_id")
  @JsonIgnore
  private ToeicPassage group;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "highlight_data", columnDefinition = "json", nullable = false)
  private HighlightData highlightData;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class HighlightData {
    private Integer start;
    private Integer end;
    private String text;
    private String color;
    private String note;
  }
}
