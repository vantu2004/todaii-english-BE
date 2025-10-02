package com.todaii.english.core.server.setting;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.todaii.english.core.entity.Setting;
import com.todaii.english.shared.enums.SettingCategory;

@Repository
public interface SettingRepository extends JpaRepository<Setting, String> {
	public List<Setting> findBySettingCategory(SettingCategory settingCategory);
}
