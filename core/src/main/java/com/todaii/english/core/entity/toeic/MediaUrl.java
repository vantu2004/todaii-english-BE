package com.todaii.english.core.entity.toeic;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

import lombok.Getter;
import lombok.Setter;

// extends chỉ là Java inheritance
// Hibernate/JPA KHÔNG tự map field từ parent class nên dùng @MappedSuperclass đê hibernate hiểu
@MappedSuperclass
@Getter
@Setter
public class MediaUrl {
  @Column(name = "image_url", length = 1024)
  private String imageUrl;

  @Column(name = "audio_url", length = 1024)
  private String audioUrl;
}
