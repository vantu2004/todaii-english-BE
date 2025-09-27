package com.todaii.english.core.admin.setting;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.todaii.english.shared.enums.SettingCategory;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SettingService {
	private final SettingRepository settingRepository;

	public List<Setting> getSettingsByCategory(SettingCategory mailServer) {
		return this.settingRepository.findBySettingCategory(mailServer);
	}

	public List<Setting> updateSettings(SettingCategory category, Map<String, String> settings) {
		// Tạo list Setting từ request map
		List<Setting> toSave = settings.entrySet().stream().map(entry -> Setting.builder().key(entry.getKey())
				.value(entry.getValue()).settingCategory(category).build()).toList();

		// saveAll: nếu key tồn tại thì update, chưa có thì insert
		return settingRepository.saveAll(toSave);
	}

}
