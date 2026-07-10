package com.todaii.english.shared.dto;

import com.todaii.english.shared.enums.Answer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDTO {
  private Long id;
  private String questionText;
  private String optionA;
  private String optionB;
  private String optionC;
  private String optionD;
  private Answer correctOption;
  private String explanation;
}
