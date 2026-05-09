package com.todaii.english.shared.request.server.toeic;

import jakarta.validation.constraints.NotBlank;

import lombok.*;

@Getter
@Setter
public class ToeicPassageRequest extends BaseToeicRequest {
  @NotBlank(message = "Passage text cannot be blank")
  private String passageText;

  @NotBlank(message = "Passage translation cannot be blank")
  private String passageTrans;
}
