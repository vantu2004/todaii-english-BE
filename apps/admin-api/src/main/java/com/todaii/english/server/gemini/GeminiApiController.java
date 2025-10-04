package com.todaii.english.server.gemini;

import org.hibernate.validator.constraints.Length;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/gemini")
public class GeminiApiController {
	private final GeminiService geminiService;

	@GetMapping("/generate")
	public ResponseEntity<?> generate(@RequestParam @NotNull @Length(min = 1, max = 1024) String prompt) {
		return ResponseEntity.ok(geminiService.askGemini(prompt));
	}

}
