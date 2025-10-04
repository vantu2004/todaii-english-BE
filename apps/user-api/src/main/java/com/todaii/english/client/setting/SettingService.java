package com.todaii.english.client.setting;

import java.util.List;

import org.springframework.stereotype.Service;

import com.todaii.english.core.entity.Setting;
import com.todaii.english.core.port.SettingQueryPort;
import com.todaii.english.shared.enums.SettingCategory;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SettingService implements SettingQueryPort {
	private final SettingRepository settingRepository;

	@Override
	public List<Setting> getSettingsByCategory(SettingCategory mailServer) {
		return this.settingRepository.findBySettingCategory(mailServer);
	}

}
