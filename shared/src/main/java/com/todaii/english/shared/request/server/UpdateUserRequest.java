package com.todaii.english.shared.request.server;

import jakarta.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequest {

  private String newPassword;

  @NotNull(message = "Display name cannot be null")
  @Length(min = 1, max = 191, message = "Display name must be between 1 and 191 characters")
  private String displayName;
}
