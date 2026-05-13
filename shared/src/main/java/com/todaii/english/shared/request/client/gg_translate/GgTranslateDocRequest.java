package com.todaii.english.shared.request.client.gg_translate;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GgTranslateDocRequest extends GgTranslateBaseRequest {
  @NotNull(message = "File is required")
  private MultipartFile multipartFile;

  @AssertTrue(message = "File must not be empty")
  public boolean isValidFile() {
    return multipartFile != null && !multipartFile.isEmpty();
  }

  @AssertTrue(message = "File size must be less than 10MB")
  public boolean isValidSize() {
    return multipartFile != null && multipartFile.getSize() <= 10 * 1024 * 1024;
  }

  @AssertTrue(message = "Only pdf, doc and docx are allowed")
  public boolean isValidExtension() {
    if (multipartFile == null || multipartFile.isEmpty()) {
      return false;
    }

    String filename = multipartFile.getOriginalFilename();

    return filename != null
        && (filename.endsWith(".pdf") || filename.endsWith("doc") || filename.endsWith(".docx"));
  }
}
