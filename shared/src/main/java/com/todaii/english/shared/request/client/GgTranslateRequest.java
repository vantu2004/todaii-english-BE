package com.todaii.english.shared.request.client;

import java.util.List;

import jakarta.validation.constraints.*;

import org.springframework.util.StringUtils;

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
  @Size(max = 20, message = "Maximum 20 texts allowed")
  private List<@NotBlank(message = "Text must not be blank") String> texts;

  @AssertTrue(message = "Total text length must be less than 1024 characters")
  public boolean isTotalTextLengthValid() {
    return getTotalCharacterCount() <= 1024;
  }

  public long getTotalCharacterCount() {
    if (texts == null) {
      return 0;
    }

    return texts.stream()
        .filter(StringUtils::hasText)
        .mapToLong(this::countUnicodeCharacters)
        .sum();
  }

  private long countUnicodeCharacters(String text) {
    return text.codePointCount(0, text.length());
  }
}
