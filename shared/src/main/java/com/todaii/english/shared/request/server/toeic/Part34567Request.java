package com.todaii.english.shared.request.server.toeic;

import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.todaii.english.shared.enums.Answer;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Part34567Request {
  // part 6 ko cần
  private String question;

  @NotBlank(message = "optionA must not be blank")
  private String optionA;

  @NotBlank(message = "optionB must not be blank")
  private String optionB;

  @NotBlank(message = "optionC must not be blank")
  private String optionC;

  @NotBlank(message = "optionD must not be blank")
  private String optionD;

  @NotNull(message = "Correct answer is required")
  private Answer correctAns;

  @NotBlank(message = "Explanation must not be blank")
  private String explanation;

  // part 5 không cần
  private Long passageId;

  @NotEmpty(message = "At least one tag must be selected")
  @Size(max = 5, message = "A maximum of 5 tags is allowed")
  private Set<Long> tagIds;
}
