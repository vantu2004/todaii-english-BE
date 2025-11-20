package com.todaii.english.server.setting;

import java.util.List;

import org.springframework.stereotype.Service;

import com.todaii.english.core.entity.Setting;
import com.todaii.english.core.port.SettingQueryPort;
import com.todaii.english.shared.enums.SettingCategory;
import com.todaii.english.shared.exceptions.BusinessException;
import com.todaii.english.shared.request.server.SettingRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SettingService implements SettingQueryPort {
	private final SettingRepository settingRepository;

	@Override
	public List<Setting> getSettingsByCategory(SettingCategory settingCategory) {
		return this.settingRepository.findBySettingCategory(settingCategory);
	}

	@Override
	public Setting getSettingByKey(String key) {
		return settingRepository.findById(key).orElseThrow(() -> new BusinessException(404, "Setting not found"));
	}

	@Deprecated
	public List<Setting> updateSettings(SettingRequest request) {
		// Tạo list Setting từ request map
		List<Setting> toSave = request.getSettings().entrySet().stream().map(entry -> Setting.builder()
				.key(entry.getKey()).value(entry.getValue()).settingCategory(request.getSettingCategory()).build())
				.toList();

		// saveAll: nếu key tồn tại thì update, chưa có thì insert
		return settingRepository.saveAll(toSave);
	}

	public Setting updateSetting(String key, String value) {
		Setting setting = getSettingByKey(key);
		setting.setValue(value);

		return settingRepository.save(setting);
	}

}
