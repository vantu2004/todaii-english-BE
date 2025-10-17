package com.todaii.english.infra.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import com.todaii.english.core.port.GeminiPort;
import com.todaii.english.shared.constants.Gemini;
import com.todaii.english.shared.exceptions.BusinessException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class GeminiApiClient implements GeminiPort {

	private final Client client;

	public GeminiApiClient(@Value("${gemini.api.key}") String apiKey) {
		this.client = Client.builder().apiKey(apiKey).build();
	}

	@Override
	public String generateText(String prompt) {
		try {
			GenerateContentResponse response = client.models.generateContent(Gemini.MODEL, prompt, null);

			if (response == null || response.text() == null) {
				log.warn("Gemini API trả về phản hồi rỗng hoặc không có text cho prompt: {}", prompt);
				throw new BusinessException(204, "Gemini API: no content returned");
			}

			return response.text();

		} catch (Exception e) {
			// Ghi log lỗi chi tiết
			log.error("Lỗi khi gọi Gemini API với prompt: {}", prompt, e);
			// Trả về fallback message (tuỳ logic của bạn)
			throw e;
		}
	}
}
