package com.todaii.english.shared.dto;

import java.time.LocalDateTime;
import java.util.Set;

import com.todaii.english.shared.enums.Answer;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ToeicQuestionDTO {
  private Long id;

  private Integer partNumber;

  private String imageUrl;
  private String audioUrl;

  private String question;
  private String optionA;
  private String optionB;
  private String optionC;
  private String optionD;

  private Answer correctAns;
  private String transcript;
  private String explanation;

  private LocalDateTime createdAt;

  private Long testId;
  private Long passageId;

  private Set<Long> tagIds;
}
