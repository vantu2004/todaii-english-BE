package com.todaii.english.shared.request.client;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportCommentRequest {
  @NotBlank(message = "Reason cannot be empty")
  @Size(max = 255, message = "Reason cannot exceed 255 characters")
  private String reason;
}
