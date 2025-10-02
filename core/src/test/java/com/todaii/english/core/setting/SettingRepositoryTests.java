package com.todaii.english.core.setting;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.todaii.english.core.entity.Setting;
import com.todaii.english.core.server.setting.SettingRepository;
import com.todaii.english.shared.enums.SettingCategory;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class SettingRepositoryTests {
	@Autowired
	private SettingRepository settingRepository;

	@Test
	public void testGetSmtpSettings() {
		List<Setting> settings = this.settingRepository.findBySettingCategory(SettingCategory.MAIL_SERVER);
		assertThat(settings).isNotEmpty();

		System.out.println(settings);
	}

	@Test
	public void testUpdateSmtpSettings() {
		// 1. Tập hợp các setting SMTP cần có
		List<Setting> smtpSettings = List.of(
				Setting.builder().key("mail_host").value("smtp.office365.com")
						.settingCategory(SettingCategory.MAIL_SERVER).build(),
				Setting.builder().key("mail_port").value("587").settingCategory(SettingCategory.MAIL_SERVER).build(),
				Setting.builder().key("mail_username").value("myaccount@outlook.com")
						.settingCategory(SettingCategory.MAIL_SERVER).build(),
				Setting.builder().key("mail_password").value("new_app_password")
						.settingCategory(SettingCategory.MAIL_SERVER).build(),
				Setting.builder().key("mail_protocol").value("smtp").settingCategory(SettingCategory.MAIL_SERVER)
						.build(),
				Setting.builder().key("mail_starttls").value("true").settingCategory(SettingCategory.MAIL_SERVER)
						.build());

		// 2. saveAll() → có thì update, không có thì insert
		settingRepository.saveAll(smtpSettings);

		// 3. Đọc lại và assert
		List<Setting> updatedSettings = settingRepository.findBySettingCategory(SettingCategory.MAIL_SERVER);

		assertThat(updatedSettings).isNotEmpty();
		assertThat(updatedSettings.stream()
				.anyMatch(s -> s.getKey().equals("mail_host") && s.getValue().equals("smtp.office365.com"))).isTrue();
		assertThat(updatedSettings.stream()
				.anyMatch(s -> s.getKey().equals("mail_username") && s.getValue().equals("myaccount@outlook.com")))
				.isTrue();
	}

}
