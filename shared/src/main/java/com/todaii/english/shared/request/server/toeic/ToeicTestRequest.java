package com.todaii.english.shared.request.server.toeic;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import com.todaii.english.shared.enums.TestStatus;
import com.todaii.english.shared.enums.TestType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ToeicTestRequest extends BaseToeicRequest {
  @NotBlank(message = "Title cannot be blank")
  @Length(max = 512, message = "Title must not exceed 512 characters")
  private String title;

  @NotNull(message = "Test type cannot be null")
  private TestType testType;

  @NotNull(message = "Duration is required")
  @Min(value = 1, message = "Duration must be greater than 0")
  private Integer duration;

  @NotBlank(message = "Description cannot be blank")
  @Length(max = 1024, message = "Description must not exceed 1024 characters")
  private String description;

  @NotNull(message = "Status cannot be null")
  private TestStatus status;

  @NotNull(message = "Collection is required")
  private Long collectionId;
}
