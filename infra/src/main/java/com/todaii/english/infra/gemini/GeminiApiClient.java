package com.todaii.english.infra.gemini;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import com.todaii.english.core.port.GeminiPort;
import com.todaii.english.shared.constants.Gemini;

@Component
public class GeminiApiClient implements GeminiPort {

	private final Client client;

	public GeminiApiClient(@Value("${gemini.api.key}") String apiKey) {
		this.client = Client.builder().apiKey(apiKey).build();
	}

	@Override
	public String generateText(String prompt) {
		GenerateContentResponse response = client.models.generateContent(Gemini.MODEL, prompt, null);

		return response.text(); // lấy text từ candidates
	}
}
