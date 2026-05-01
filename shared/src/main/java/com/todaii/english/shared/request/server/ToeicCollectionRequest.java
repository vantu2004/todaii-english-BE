package com.todaii.english.shared.request.server;

import jakarta.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ToeicCollectionRequest {

  @NotNull(message = "Collection name cannot be null")
  @Length(max = 191, message = "Collection name must not exceed 191 characters")
  private String name;

  @NotNull(message = "Collection description cannot be null")
  @Length(max = 1024, message = "Collection description must not exceed 1024 characters")
  private String description;
}
