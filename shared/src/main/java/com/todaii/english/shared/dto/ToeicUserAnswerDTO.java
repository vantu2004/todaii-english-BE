package com.todaii.english.shared.dto;

import com.todaii.english.shared.enums.Answer;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ToeicUserAnswerDTO {
  private Long id;
  private Long sessionId;
  private Long questionId;
  private Integer partNumber;
  private Answer userChoice;
  private Answer correctChoice;
  private Integer status;
  private Boolean isMarked;
  private String questionText;
  private String optionA;
  private String optionB;
  private String optionC;
  private String optionD;
  private String explanation;
  private String transcript;
  private String imageUrl;
  private String audioUrl;
}
