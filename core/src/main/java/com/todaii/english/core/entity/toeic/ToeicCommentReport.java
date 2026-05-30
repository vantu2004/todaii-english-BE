package com.todaii.english.core.entity.toeic;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.todaii.english.core.entity.user.User;

import lombok.*;

@Entity
@Table(name = "toeic_comment_reports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ToeicCommentReport {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "comment_id", nullable = false)
  @JsonIgnore
  private ToeicComment comment;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "reported_by", nullable = false)
  @JsonIgnore
  private User reportedBy;

  @Column(nullable = false, length = 255)
  private String reason;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;
}
