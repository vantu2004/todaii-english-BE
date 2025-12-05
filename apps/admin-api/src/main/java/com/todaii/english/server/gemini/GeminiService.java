package com.todaii.english.server.gemini;

import org.springframework.stereotype.Service;

import com.todaii.english.core.port.GeminiPort;
import com.todaii.english.shared.response.AIResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GeminiService {
	private final GeminiPort geminiPort;

	public AIResponse askGemini(String prompt) {
		return geminiPort.generateText(prompt);
	}

}
