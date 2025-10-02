package com.todaii.english.core.setting;

import java.util.List;

import com.todaii.english.core.entity.Setting;
import com.todaii.english.shared.enums.SettingCategory;

public interface SettingQueryPort {
	List<Setting> getSettingsByCategory(SettingCategory category);
}
