package com.todaii.english.shared.response;

import java.time.LocalDateTime;

import com.todaii.english.shared.enums.TopicType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContentProgressResponse {
  private Long contentId;
  private TopicType contentType;
  private Integer estimateTime;
  private Integer studyTime;
  private Float position;
  private Boolean completed;
  private LocalDateTime updatedAt;
}
