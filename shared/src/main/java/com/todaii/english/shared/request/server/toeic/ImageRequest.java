package com.todaii.english.shared.request.server.toeic;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImageRequest {
  // optional, upload trực tiếp
  @Length(max = 1024, message = "Image URL must not exceed 1024 characters")
  @URL(message = "Image URL must be a valid URL")
  private String uploadedImage;

  // optional, chỉ gửi url ko upload trực tiếp
  @Length(max = 1024, message = "Image URL must not exceed 1024 characters")
  @URL(message = "Image URL must be a valid URL")
  private String imageUrl;
}
