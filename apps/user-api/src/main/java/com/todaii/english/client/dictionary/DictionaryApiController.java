package com.todaii.english.client.dictionary;

import java.util.List;

import org.hibernate.validator.constraints.Length;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.todaii.english.client.UserUtils;
import com.todaii.english.core.entity.DictionaryEntry;
import com.todaii.english.shared.response.DictionaryApiResponse;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/dictionary")
public class DictionaryApiController {
	private final DictionaryService dictionaryService;

	@GetMapping("/raw-word")
	public ResponseEntity<DictionaryApiResponse[]> getRawWord(Authentication authentication,
			@RequestParam String word) {
		Long currentUserId = null;
		if (authentication != null && authentication.isAuthenticated()) {
			currentUserId = UserUtils.getCurrentAdminId(authentication);
		}

		DictionaryApiResponse[] dictionaryApiResponses = dictionaryService.lookupWord(currentUserId, word);
		return ResponseEntity.ok(dictionaryApiResponses);
	}

	@GetMapping("/{id}")
	public ResponseEntity<DictionaryEntry> getWordById(@PathVariable Long id) {
		return ResponseEntity.ok(dictionaryService.findById(id));
	}

	@GetMapping("/headword")
	public ResponseEntity<List<DictionaryEntry>> getWordByHeadword(
			@RequestParam @NotBlank(message = "Word cannot be blank") @Length(max = 64, message = "Word must not exceed 64 characters") String word) {
		return ResponseEntity.ok(dictionaryService.findByHeadword(word));
	}

	@GetMapping("/gemini")
	public ResponseEntity<List<DictionaryEntry>> getWordByGemini(Authentication authentication,
			@RequestParam @NotBlank(message = "Word cannot be blank") @Length(max = 64, message = "Word must not exceed 64 characters") String word)
			throws Exception {
		Long currentUserId = null;
		if (authentication != null && authentication.isAuthenticated()) {
			currentUserId = UserUtils.getCurrentAdminId(authentication);
		}

		return ResponseEntity.ok(dictionaryService.getWordByGemini(currentUserId, word));
	}

	@GetMapping("/related-word")
	public ResponseEntity<List<String>> getRelatedWord(Authentication authentication,
			@RequestParam @NotBlank(message = "Word cannot be blank") @Length(max = 64, message = "Word must not exceed 64 characters") String word) {
		Long currentUserId = null;
		if (authentication != null && authentication.isAuthenticated()) {
			currentUserId = UserUtils.getCurrentAdminId(authentication);
		}

		return ResponseEntity.ok(dictionaryService.getRelatedWord(currentUserId, word));
	}

	@GetMapping("/ask-gemini")
	public ResponseEntity<String> generate(Authentication authentication,
			@RequestParam @NotBlank(message = "Prompt must not be blank") @Length(min = 1, max = 1024, message = "Prompt must be between 1 and 1024 characters") String question) {
		Long currentUserId = null;
		if (authentication != null && authentication.isAuthenticated()) {
			currentUserId = UserUtils.getCurrentAdminId(authentication);
		}

		return ResponseEntity.ok(dictionaryService.askGemini(currentUserId, question));
	}
}
