package com.todaii.english.shared.request.server;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.todaii.english.shared.enums.Answer;
import com.todaii.english.shared.enums.TopicType;

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
public class QuestionRequest {
  private TopicType topicType;
  private Long contentId;

  @NotBlank(message = "Question text cannot be blank")
  private String questionText;

  @NotBlank(message = "Option A cannot be blank")
  private String optionA;

  @NotBlank(message = "Option B cannot be blank")
  private String optionB;

  @NotBlank(message = "Option C cannot be blank")
  private String optionC;

  @NotBlank(message = "Option D cannot be blank")
  private String optionD;

  @NotNull(message = "Correct option is required")
  private Answer correctOption;

  @NotBlank(message = "Explanation cannot be blank")
  private String explanation;
}
