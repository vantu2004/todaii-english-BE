package com.todaii.english.shared.dto.toeic;

import java.time.LocalDateTime;
import java.util.Set;

import com.todaii.english.shared.enums.Answer;

import lombok.*;

@Getter
@Setter
public class ToeicQuestionDTO {
  private Long id;
  private Long passageId;

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

  private Set<ToeicTagDTO> tags;

  // vì shared ko truy cập vào core nên phải tự tạo nested class
  @Getter
  @Setter
  public static class ToeicTagDTO {
    private Long id;
    private String name;
    private String alias;
  }
}
