package com.todaii.english.shared.dto.toeic;

import java.time.LocalDateTime;

import com.todaii.english.shared.enums.TestStatus;
import com.todaii.english.shared.enums.TestType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ToeicTestDTO {
  private Long id;
  private String title;
  private TestType testType;
  private Integer duration;
  private String imageUrl;
  private String audioUrl;
  private String description;
  private TestStatus status;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private ToeicCollectionDTO collection;
  private AdminDTO createdBy;
  private AdminDTO updatedBy;

  @Getter
  @Setter
  public static class ToeicCollectionDTO {
    private Long id;
    private String name;
    private String alias;
  }

  @Getter
  @Setter
  public static class AdminDTO {
    private Long id;
    private String displayName;
  }
}
