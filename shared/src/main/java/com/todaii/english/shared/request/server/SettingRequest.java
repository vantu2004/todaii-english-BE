package com.todaii.english.shared.request.server;

import java.util.Map;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

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
          @NotBlank(message = "Key must not be blank")
          @Length(max = 1014, message = "Key must be less than 128 characters") String,
          @NotBlank(message = "Value must not be blank")
          @Length(max = 1014, message = "Value must be less than 1024 characters") String>
      settings;
}
