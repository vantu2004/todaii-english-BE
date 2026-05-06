package com.todaii.english.shared.dto;

import java.util.Set;

import jakarta.validation.constraints.NotNull;

import com.todaii.english.shared.enums.Answer;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ToeicQuestionDTO {

  private Long id;

  private Long testId;
  private Long passageId;

  @NotNull private Integer partNumber;

  private Integer questionNo;

  private String question;
  private String imageUrl;
  private String audioUrl;

  private String optionA;
  private String optionB;
  private String optionC;
  private String optionD;

  @NotNull private Answer correctAns;

  private String explanation;
  private String translation;

  private Set<Long> tagIds;
}
