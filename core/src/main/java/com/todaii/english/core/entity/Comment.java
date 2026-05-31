package com.todaii.english.core.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.todaii.english.core.entity.admin.Admin;
import com.todaii.english.core.entity.user.User;
import com.todaii.english.shared.enums.CommentStatus;
import com.todaii.english.shared.enums.CommentTargetType;

import lombok.*;

@Entity
@Table(name = "comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(name = "target_type", nullable = false, length = 32)
  private CommentTargetType targetType;

  @Column(name = "target_id", nullable = false)
  private Long targetId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  @JsonIgnore
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "admin_id")
  @JsonIgnore
  private Admin admin;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_id")
  @JsonIgnore
  private Comment parent;

  @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  @JsonIgnore
  private List<Comment> replies = new ArrayList<>();

  @Column(columnDefinition = "MEDIUMTEXT", nullable = false)
  private String content;

  @Enumerated(EnumType.STRING)
  @Builder.Default
  @Column(nullable = false, length = 32)
  private CommentStatus status = CommentStatus.APPROVED;

  @Builder.Default
  @Column(name = "is_auto_flagged", nullable = false)
  private Boolean isAutoFlagged = false;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;
}
