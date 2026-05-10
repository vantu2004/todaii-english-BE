package com.todaii.english.core.entity.toeic;

import jakarta.persistence.Column;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MediaUrl {
  @Column(name = "image_url", length = 1024)
  private String imageUrl;

  @Column(name = "audio_url", length = 1024)
  private String audioUrl;
}
