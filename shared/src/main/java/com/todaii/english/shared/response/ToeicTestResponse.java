package com.todaii.english.shared.response;

import com.todaii.english.shared.enums.TestStatus;
import com.todaii.english.shared.enums.TestType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ToeicTestResponse {
  private Long id;
  private String title;
  private TestType testType;
  private Integer duration;
  private String audioUrl;
  private String thumbnail;
  private String description;
  private TestStatus status;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private ToeicCollectionDTO collection;
  private AdminDTO createdBy;
  private AdminDTO updatedBy;

  @Getter
  @Setter
  public static class ToeicCollectionDTO{
    private Long id;
    private String name;
    private String alias;
  }

  @Getter
  @Setter
  public static class AdminDTO{
    private Long id;
    private String name;
  }
}
