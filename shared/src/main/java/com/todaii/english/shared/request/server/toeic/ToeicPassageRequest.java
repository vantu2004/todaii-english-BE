package com.todaii.english.shared.request.server.toeic;

import jakarta.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

import lombok.*;

@Getter
@Setter
public class ToeicPassageRequest {
  // optional, upload trực tiếp
  @Length(max = 1024, message = "Image URL must not exceed 1024 characters")
  @URL(message = "Image URL must be a valid URL")
  private String uploadedImage;

  // optional, chỉ gửi url ko upload trực tiếp
  @Length(max = 1024, message = "Image URL must not exceed 1024 characters")
  @URL(message = "Image URL must be a valid URL")
  private String imageUrl;

  // optional
  @Length(max = 1024, message = "Audio URL must not exceed 1024 characters")
  @URL(message = "Audio URL must be a valid URL")
  private String uploadedAudio;

  // optional
  @Length(max = 1024, message = "Audio URL must not exceed 1024 characters")
  @URL(message = "Audio URL must be a valid URL")
  private String audioUrl;

  @NotBlank(message = "Passage text cannot be blank")
  private String passageText;

  @NotBlank(message = "Passage translation cannot be blank")
  private String passageTrans;
}
