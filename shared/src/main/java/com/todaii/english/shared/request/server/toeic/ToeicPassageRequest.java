package com.todaii.english.shared.request.server.toeic;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import lombok.*;

@Getter
@Setter
public class ToeicPassageRequest {
  @NotBlank(message = "Passage text cannot be blank")
  private String passageText;

  @NotBlank(message = "Passage translation cannot be blank")
  private String passageTrans;

  @Valid private ImageRequest imageRequest;

  @Valid private AudioRequest audioRequest;
}
