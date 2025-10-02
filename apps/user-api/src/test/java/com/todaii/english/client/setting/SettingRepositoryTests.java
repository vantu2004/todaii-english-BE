package com.todaii.english.client.setting;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.todaii.english.core.entity.Setting;
import com.todaii.english.shared.enums.SettingCategory;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class SettingRepositoryTests {
	@Autowired
	private SettingRepository settingRepository;

	@Test
	public void testFindBySettingCategory() {
		List<Setting> settings = settingRepository.findBySettingCategory(SettingCategory.MAIL_SERVER);

		assertThat(settings).isNotEmpty();
		assertThat(settings).extracting(Setting::getKey).contains("mail_username");
		assertThat(settings).extracting(Setting::getValue).contains("tulevan526@gmail.com");
		assertThat(settings).extracting(Setting::getSettingCategory).contains(SettingCategory.MAIL_SERVER);
	}
}
