package com.todaii.english.infra.config;

import java.util.List;
import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import com.todaii.english.core.entity.Setting;
import com.todaii.english.core.port.SettingQueryPort;
import com.todaii.english.shared.constants.SettingKey;
import com.todaii.english.shared.enums.SettingCategory;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SmtpConfig {
	private final SettingQueryPort settingQueryPort;

	@Bean
	public JavaMailSender createMailSender() {
		List<Setting> settings = this.settingQueryPort.getSettingsByCategory(SettingCategory.MAIL_SERVER);

		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		Properties props = mailSender.getJavaMailProperties();

		for (Setting s : settings) {
			switch (s.getKey()) {
			case SettingKey.MAIL_HOST -> mailSender.setHost(s.getValue());
			case SettingKey.MAIL_PORT -> mailSender.setPort(Integer.parseInt(s.getValue()));
			case SettingKey.MAIL_USERNAME -> mailSender.setUsername(s.getValue());
			case SettingKey.MAIL_PASSWORD -> mailSender.setPassword(s.getValue());
			case SettingKey.MAIL_PROTOCOL -> props.put("mail.transport.protocol", s.getValue());
			case SettingKey.MAIL_STARTTLS -> {
				props.put("mail.smtp.starttls.enable", s.getValue());
				props.put("mail.smtp.auth", "true");
			}
			}
		}

		return mailSender;
	}
}
