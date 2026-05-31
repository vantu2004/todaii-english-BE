package com.todaii.english.shared.request.client;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.todaii.english.shared.enums.CommentTargetType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateCommentRequest {
  @NotBlank(message = "Comment content cannot be empty")
  private String content;

  @NotNull(message = "Target type is required")
  private CommentTargetType targetType;

  @NotNull(message = "Target ID is required")
  private Long targetId;

  private Long parentId;
}
