package com.todaii.english.admin.setting;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.todaii.english.core.admin.setting.Setting;
import com.todaii.english.core.admin.setting.SettingService;
import com.todaii.english.shared.enums.SettingCategory;
import com.todaii.english.shared.request.admin.SettingRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/setting")
public class SettingApiController {
	private final SettingService settingService;

	@GetMapping("/smtp")
	public ResponseEntity<?> getSmtpSettings() {
		List<Setting> settings = this.settingService.getSettingsByCategory(SettingCategory.MAIL_SERVER);
		if (CollectionUtils.isEmpty(settings)) {
			return ResponseEntity.noContent().build();
		}

		return ResponseEntity.ok().body(settings);
	}

	@PutMapping("/smtp")
	public ResponseEntity<?> updateSmtpSettings(@Valid @RequestBody SettingRequest request) {
		List<Setting> updated = this.settingService.updateSettings(SettingCategory.MAIL_SERVER, request.getSettings());

		return ResponseEntity.ok(updated);
	}

}
