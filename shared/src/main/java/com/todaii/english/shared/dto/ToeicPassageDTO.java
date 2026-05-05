package com.todaii.english.shared.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ToeicPassageDTO {

  private Long id;

  @NotNull(message = "Test_id cannot be null")
  private Long testId;

  private String testTitle;

  @NotNull(message = "Part number cannot be null")
  private Integer partNumber;

  @NotBlank(message = "Passage text cannot be blank")
  @Length(max = 191, message = "Passage text must not exceed 191 characters")
  private String passageText;

  @NotBlank(message = "Passage translation cannot be blank")
  @Length(max = 1024, message = "Passage translation must not exceed 1024 characters")
  private String passageTrans;

  @NotBlank(message = "Image url cannot be blank")
  @Length(max = 1024, message = "Image url must not exceed 1024 characters")
  private String imageUrl;

  @NotBlank(message = "Audio url cannot be blank")
  @Length(max = 1024, message = "Audio url must not exceed 1024 characters")
  private String audioUrl;
}
