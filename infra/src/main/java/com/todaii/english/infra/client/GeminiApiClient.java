package com.todaii.english.infra.client;

import org.springframework.stereotype.Component;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import com.todaii.english.core.port.GeminiPort;
import com.todaii.english.core.port.SettingQueryPort;
import com.todaii.english.shared.constants.SettingKey;
import com.todaii.english.shared.exceptions.BusinessException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class GeminiApiClient implements GeminiPort {
	private final Client client;
	private final SettingQueryPort settingQueryPort;

	public GeminiApiClient(SettingQueryPort settingQueryPort) {
		this.settingQueryPort = settingQueryPort;

		String apiKey = settingQueryPort.getSettingByKey(SettingKey.GEMINI_API_KEY).getValue();
		this.client = Client.builder().apiKey(apiKey).build();
	}

	@Override
	public String generateText(String prompt) {
		try {
			GenerateContentResponse response = client.models.generateContent(
					settingQueryPort.getSettingByKey(SettingKey.GEMINI_MODEL).getValue(), prompt, null);

			if (response == null || response.text() == null) {
				log.warn("Gemini API trả về phản hồi rỗng hoặc không có text cho prompt: {}", prompt);
				throw new BusinessException(204, "Gemini API: no content returned");
			}

			String responseText = response.text().replaceAll("```json", "").replaceAll("```", "").trim();

			return responseText;

		} catch (Exception e) {
			log.error("Lỗi khi gọi Gemini API với prompt: {}", prompt, e);
			throw e;
		}
	}
}
