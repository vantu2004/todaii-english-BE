package com.todaii.english.shared.request.server;

import java.util.Map;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import com.todaii.english.shared.enums.SettingCategory;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SettingRequest {
  @NotNull(message = "Setting category cannot be null")
  private SettingCategory settingCategory;

  @NotEmpty(message = "Settings map cannot be empty")
  private Map<
          @NotBlank(message = "Setting key cannot be blank") String,
          @NotBlank(message = "Setting value cannot be blank") String>
      settings;
}
