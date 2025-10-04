package com.todaii.english.server.gemini;

import org.springframework.stereotype.Service;

import com.todaii.english.core.port.GeminiPort;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GeminiService {
	private final GeminiPort geminiPort;

	public String askGemini(String prompt) {
		return geminiPort.generateText(prompt);
	}

}
