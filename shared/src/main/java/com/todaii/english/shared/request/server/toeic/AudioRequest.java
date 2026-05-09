package com.todaii.english.shared.request.server.toeic;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AudioRequest {
  // optional
  @Length(max = 1024, message = "Audio URL must not exceed 1024 characters")
  @URL(message = "Audio URL must be a valid URL")
  private String uploadedAudio;

  // optional
  @Length(max = 1024, message = "Audio URL must not exceed 1024 characters")
  @URL(message = "Audio URL must be a valid URL")
  private String audioUrl;
}
