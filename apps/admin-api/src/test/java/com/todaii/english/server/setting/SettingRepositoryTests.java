package com.todaii.english.server.setting;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.todaii.english.core.entity.Setting;
import com.todaii.english.shared.constants.SettingKey;
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
	
    @Test
    public void testAddSettings() {
        // GEMINI
        Setting geminiApiKey = Setting.builder()
                .key(SettingKey.GEMINI_API_KEY)
                .value("abc")
                .settingCategory(SettingCategory.GEMINI)
                .build();

        Setting geminiModel = Setting.builder()
                .key(SettingKey.GEMINI_MODEL)
                .value("abc") // giả sử model muốn test
                .settingCategory(SettingCategory.GEMINI)
                .build();

        // NEWS API
        Setting newsApiKey = Setting.builder()
                .key(SettingKey.NEWSAPI_API_KEY)
                .value("abc")
                .settingCategory(SettingCategory.NEWS_API)
                .build();

        // YOUTUBE
        Setting youtubeApiKey = Setting.builder()
                .key(SettingKey.YOUTUBE_API_KEY)
                .value("abc")
                .settingCategory(SettingCategory.YOUTUBE)
                .build();

        // CLOUDINARY
        Setting cloudinaryName = Setting.builder()
                .key(SettingKey.CLOUDINARY_CLOUD_NAME)
                .value("abc")
                .settingCategory(SettingCategory.CLOUDINARY)
                .build();

        Setting cloudinaryApiKey = Setting.builder()
                .key(SettingKey.CLOUDINARY_API_KEY)
                .value("abc")
                .settingCategory(SettingCategory.CLOUDINARY)
                .build();

        Setting cloudinaryApiSecret = Setting.builder()
                .key(SettingKey.CLOUDINARY_API_SECRET)
                .value("abc")
                .settingCategory(SettingCategory.CLOUDINARY)
                .build();

        // Lưu tất cả vào database
        settingRepository.save(geminiApiKey);
        settingRepository.save(geminiModel);
        settingRepository.save(newsApiKey);
        settingRepository.save(youtubeApiKey);
        settingRepository.save(cloudinaryName);
        settingRepository.save(cloudinaryApiKey);
        settingRepository.save(cloudinaryApiSecret);

        System.out.println("All settings added successfully!");
    }

}
