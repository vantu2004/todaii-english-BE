package com.todaii.english.shared.request.server.toeic;

import java.util.Set;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.todaii.english.shared.enums.Answer;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Part01Request {
  @NotNull(message = "Correct answer is required")
  private Answer correctAns;

  @NotBlank(message = "Transcript must not be blank")
  private String transcript;

  @NotBlank(message = "Explanation must not be blank")
  private String explanation;

  @NotEmpty(message = "At least one tag must be selected")
  @Size(max = 5, message = "A maximum of 5 tags is allowed")
  private Set<Long> tagIds;

  @Valid
  private ImageRequest imageRequest;

  @Valid
  private AudioRequest audioRequest;
}
