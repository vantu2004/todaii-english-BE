package com.todaii.english.core.port;

import com.todaii.english.shared.response.AIResponse;

public interface GeminiPort {
	AIResponse generateText(String prompt);
}
