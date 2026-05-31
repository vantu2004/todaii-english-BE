package com.todaii.english.shared.dto;

import java.time.LocalDateTime;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ToeicUserHighlightDTO {
  private Long id;
  private Long sessionId;
  private Long questionId;
  private Long groupId;
  
  private Integer start;
  private Integer end;
  private String text;
  private String color;
  private String note;
  
  private LocalDateTime createdAt;
}
