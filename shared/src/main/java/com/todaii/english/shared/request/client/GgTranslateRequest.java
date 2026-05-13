package com.todaii.english.shared.request.client;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GgTranslateRequest {
  @NotBlank(message = "Source language must not be blank")
  private String sourceLanguage;

  @NotBlank(message = "Target language must not be blank")
  private String targetLanguage;

  @NotNull(message = "Texts must not be null")
  @NotEmpty(message = "Texts must not be empty")
  private List<String> texts;
}
