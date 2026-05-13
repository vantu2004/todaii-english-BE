package com.todaii.english.shared.request.client.gg_translate;

import jakarta.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GgTranslateBaseRequest {
  @NotBlank(message = "Source language must not be blank")
  private String sourceLanguage;

  @NotBlank(message = "Target language must not be blank")
  private String targetLanguage;
}
