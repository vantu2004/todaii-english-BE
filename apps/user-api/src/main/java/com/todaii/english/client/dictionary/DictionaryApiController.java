package com.todaii.english.client.dictionary;

import java.util.List;

import org.hibernate.validator.constraints.Length;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.todaii.english.core.entity.DictionaryEntry;
import com.todaii.english.shared.response.DictionaryApiResponse;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/dictionary")
@Tag(name = "Dictionary", description = "APIs for word lookup, dictionary entries, and AI-generated word responses")
public class DictionaryApiController {

	private final DictionaryService dictionaryService;

	@GetMapping("/raw-word")
	@Operation(summary = "Lookup raw word", description = "Lookup a word in the dictionary API and return raw data")
	public ResponseEntity<DictionaryApiResponse[]> getRawWord(@RequestParam String word) {
		DictionaryApiResponse[] dictionaryApiResponses = dictionaryService.lookupWord(word);
		return ResponseEntity.ok(dictionaryApiResponses);
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get word by ID", description = "Fetch a dictionary entry by its ID")
	public ResponseEntity<DictionaryEntry> getWordById(@PathVariable Long id) {
		return ResponseEntity.ok(dictionaryService.findById(id));
	}

	@GetMapping("/headword")
	@Operation(summary = "Get words by headword", description = "Fetch dictionary entries that match the headword")
	public ResponseEntity<List<DictionaryEntry>> getWordByHeadword(
			@RequestParam @NotBlank(message = "Word cannot be blank")
			@Length(max = 64, message = "Word must not exceed 64 characters") String word) {
		return ResponseEntity.ok(dictionaryService.findByHeadword(word));
	}

	@GetMapping("/gemini")
	@Operation(summary = "Get words by Gemini AI", description = "Fetch dictionary entries using Gemini AI for enhanced results")
	public ResponseEntity<List<DictionaryEntry>> getWordByGemini(
			@RequestParam @NotBlank(message = "Word cannot be blank")
			@Length(max = 64, message = "Word must not exceed 64 characters") String word) throws Exception {
		return ResponseEntity.ok(dictionaryService.getWordByGemini(word));
	}

	@GetMapping("/related-word")
	@Operation(summary = "Get related words", description = "Fetch a list of words related to the given word")
	public ResponseEntity<List<String>> getRelatedWord(
			@RequestParam @NotBlank(message = "Word cannot be blank")
			@Length(max = 64, message = "Word must not exceed 64 characters") String word) {
		return ResponseEntity.ok(dictionaryService.getRelatedWord(word));
	}

	@GetMapping("/ask-gemini")
	@Operation(summary = "Ask Gemini AI", description = "Generate a response from Gemini AI based on a prompt")
	public ResponseEntity<String> generate(
			@RequestParam @NotBlank(message = "Prompt must not be blank")
			@Length(min = 1, max = 1024, message = "Prompt must be between 1 and 1024 characters") String question) {
		return ResponseEntity.ok(dictionaryService.askGemini(question));
	}
}
