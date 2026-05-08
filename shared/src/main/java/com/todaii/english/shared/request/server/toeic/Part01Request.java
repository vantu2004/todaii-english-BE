package com.todaii.english.shared.request.server.toeic;

import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

import com.todaii.english.shared.enums.Answer;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Part01Request {
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

  @NotNull(message = "Correct answer is required")
  private Answer correctAns;

  @NotBlank(message = "Transcript must not be blank")
  private String transcript;

  @NotBlank(message = "Explanation must not be blank")
  private String explanation;

  @NotNull(message = "Test ID is required")
  private Long testId;

  @NotEmpty(message = "At least one tag must be selected")
  @Size(max = 5, message = "A maximum of 5 tags is allowed")
  private Set<Long> tagIds;
}
