package com.todaii.english.shared.dto.toeic;

import com.todaii.english.shared.enums.Answer;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ToeicUserAnswerDTO {
  private Long id;
  private Answer userChoice;
  private Integer status;
  private Boolean isMarked;

  private Long questionId;
}
