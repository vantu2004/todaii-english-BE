package com.todaii.english.infra.client;

import org.springframework.stereotype.Component;

import com.google.genai.Client;
import com.google.genai.types.CountTokensResponse;
import com.google.genai.types.GenerateContentResponse;
import com.todaii.english.core.port.GeminiPort;
import com.todaii.english.core.port.SettingQueryPort;
import com.todaii.english.shared.constants.SettingKey;
import com.todaii.english.shared.exceptions.BusinessException;
import com.todaii.english.shared.response.AIResponse;

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
	public AIResponse generateText(String prompt) {
		try {
			GenerateContentResponse response = client.models.generateContent(
					settingQueryPort.getSettingByKey(SettingKey.GEMINI_MODEL).getValue(), prompt, null);

			if (response == null || response.text() == null) {
				log.warn("Gemini API trả về phản hồi rỗng hoặc không có text cho prompt: {}", prompt);
				throw new BusinessException(204, "Gemini API: no content returned");
			}

			String responseText = response.text().replaceAll("```json", "").replaceAll("```", "").trim();

			int[] tokens = countTokens(settingQueryPort.getSettingByKey(SettingKey.GEMINI_MODEL).getValue(), prompt,
					responseText);

			return new AIResponse(responseText, tokens[0], tokens[1]);

		} catch (Exception e) {
			log.error("Lỗi khi gọi Gemini API với prompt: {}", prompt, e);
			throw e;
		}
	}

	/**
	 * Tính token của prompt (input) và response (output) cho model Gemini.
	 *
	 * @param modelId      ID của model
	 * @param prompt       Text input
	 * @param responseText Text output
	 * @return int[] : [0] = inputToken, [1] = outputToken
	 */
	private int[] countTokens(String modelId, String prompt, String responseText) {
		int inputToken = 0;
		int outputToken = 0;

		try {
			if (prompt != null && !prompt.isEmpty()) {
				CountTokensResponse inputResp = client.models.countTokens(modelId, prompt, null);
				inputToken = inputResp.totalTokens().orElse(0);
			}

			if (responseText != null && !responseText.isEmpty()) {
				CountTokensResponse outputResp = client.models.countTokens(modelId, responseText, null);
				outputToken = outputResp.totalTokens().orElse(0);
			}

		} catch (Exception e) {
			log.error("Lỗi khi tính token với model {}: prompt='{}', response='{}'", modelId, prompt, responseText, e);
		}

		return new int[] { inputToken, outputToken };
	}
}
