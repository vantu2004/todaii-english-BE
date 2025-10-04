package com.todaii.english.server.dictionary;

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
@RequestMapping("/api/v1/dictionary")
public class DictionaryApiController {
	private final DictionaryService dictionaryService;

	@GetMapping("/raw-word")
	public ResponseEntity<?> getVocabFromDictionaryApi(@NotNull @Length(min = 1, max = 191) String word) {
		return ResponseEntity.ok(dictionaryService.lookupWord(word));
	}

	@GetMapping("/search")
	public ResponseEntity<?> enrichDictionary(@RequestParam @NotNull @Length(min = 1, max = 64) String word)
			throws Exception {
		return ResponseEntity.ok(dictionaryService.search(word));
	}
}
