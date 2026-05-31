package com.todaii.english.shared.dto;

import java.time.LocalDateTime;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentReportDTO {
  private Long id;
  private Long commentId;
  private String commentContent;
  private String commentUserDisplayName;
  private Long reportedById;
  private String reportedByDisplayName;
  private String reason;
  private LocalDateTime createdAt;
}
