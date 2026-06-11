package com.todaii.english.core.entity.user;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.todaii.english.core.entity.NotebookItem;
import com.todaii.english.core.entity.article.Article;
import com.todaii.english.core.entity.toeic.ToeicTest;
import com.todaii.english.core.entity.video.Video;
import com.todaii.english.core.security.JwtPrincipal;
import com.todaii.english.shared.enums.UserStatus;

import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements JwtPrincipal {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // email tối đa 191 ký tự vì dùng unique index + utf8mb4
  @Column(nullable = false, unique = true, length = 191)
  private String email;

  @Column(name = "password_hash", length = 191)
  private String passwordHash;

  // google_sub unique index
  @Column(name = "google_sub", unique = true, length = 191)
  private String googleSub;

  @Column(name = "display_name", nullable = false, length = 191)
  private String displayName;

  @Column(name = "avatar_url", length = 512)
  private String avatarUrl;

  @Column(name = "otp", length = 16)
  private String otp;

  @Column(name = "otp_expired_at")
  private LocalDateTime otpExpiredAt;

  @Column(name = "reset_password_token", length = 64)
  private String resetPasswordToken;

  @Column(name = "reset_password_expired_at")
  private LocalDateTime resetPasswordExpiredAt;

  @Builder.Default private Boolean enabled = false;

  @Enumerated(EnumType.STRING)
  @Column(length = 32)
  private UserStatus status;

  @Column(name = "email_verified_at")
  private LocalDateTime emailVerifiedAt;

  @Column(name = "last_login_at")
  private LocalDateTime lastLoginAt;

  @Builder.Default
  @Column(name = "is_deleted", nullable = false)
  private Boolean isDeleted = false;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  @Column(name = "deleted_at")
  private LocalDateTime deletedAt;

  @ManyToMany
  @JoinTable(
      name = "article_users",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "article_id"))
  @Builder.Default
  @JsonIgnore
  private Set<Article> savedArticles = new HashSet<>();

  @ManyToMany
  @JoinTable(
      name = "video_users",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "video_id"))
  @Builder.Default
  @JsonIgnore
  private Set<Video> savedVideos = new HashSet<>();

  @ManyToMany
  @JoinTable(
      name = "test_users",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "test_id"))
  @Builder.Default
  @JsonIgnore
  private Set<ToeicTest> savedTests = new HashSet<>();

  @OneToMany(
      mappedBy = "user",
      cascade = CascadeType.ALL,
      orphanRemoval = true,
      fetch = FetchType.LAZY)
  @Builder.Default
  @JsonIgnore
  private Set<NotebookItem> notebookItems = new HashSet<>();

  @JsonIgnore
  @Override
  public String getActorType() {
    return "USER";
  }

  @Override
  public Long getId() {
    return this.id;
  }

  @Override
  public String getDisplayName() {
    return this.displayName;
  }
}
