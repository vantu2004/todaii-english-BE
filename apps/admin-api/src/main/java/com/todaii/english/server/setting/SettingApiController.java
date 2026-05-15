package com.todaii.english.server.setting;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.todaii.english.core.entity.Setting;
import com.todaii.english.shared.enums.SettingCategory;
import com.todaii.english.shared.request.server.SettingRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/setting")
public class SettingApiController {
  private final SettingService settingService;

  @GetMapping
  public ResponseEntity<List<Setting>> getSettings(@RequestParam SettingCategory category) {
    return ResponseEntity.ok(settingService.getSettingsByCategory(category));
  }

  @PostMapping
  public ResponseEntity<List<Setting>> updateSettings(@Valid @RequestBody SettingRequest request) {
    return ResponseEntity.ok(settingService.updateSettings(request));
  }

  @Deprecated
  public ResponseEntity<Setting> updateSetting(
      @PathVariable String key,
      @RequestParam
          @NotBlank(message = "Value cannot be blank")
          @Length(max = 1014, message = "Value must be less than 1024 characters")
          String value) {
    return ResponseEntity.ok(settingService.updateSetting(key, value));
  }
}
