package com.todaii.english.shared.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.todaii.english.shared.enums.CommentStatus;
import com.todaii.english.shared.enums.CommentTargetType;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDTO {
  private Long id;
  private CommentTargetType targetType;
  private Long targetId;

  // User info
  private Long userId;
  private String userDisplayName;
  private String userAvatarUrl;

  // Admin info
  private Long adminId;
  private String adminDisplayName;
  private String adminAvatarUrl;

  private Long parentId;
  private String content;
  private CommentStatus status;
  private Boolean isAutoFlagged;

  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  @Builder.Default private List<CommentDTO> replies = new ArrayList<>();
}
