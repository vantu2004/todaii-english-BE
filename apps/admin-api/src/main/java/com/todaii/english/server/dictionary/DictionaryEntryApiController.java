package com.todaii.english.server.dictionary;

import java.util.List;

import org.hibernate.validator.constraints.Length;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.todaii.english.core.entity.DictionaryEntry;
import com.todaii.english.shared.dto.DictionaryEntryDTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/dictionary")
public class DictionaryEntryApiController {
	private final DictionaryEntryService dictionaryService;

	@GetMapping
	public ResponseEntity<?> getAllWords() {
		List<DictionaryEntry> dictionaryEntries = dictionaryService.findAll();
		if (dictionaryEntries.isEmpty()) {
			return ResponseEntity.noContent().build();
		}

		return ResponseEntity.ok(dictionaryEntries);
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getWord(@PathVariable Long id) {
		return ResponseEntity.ok(dictionaryService.findById(id));
	}

	@PostMapping("/gemini")
	public ResponseEntity<?> createWordByGemini(@RequestParam @NotNull @Length(min = 1, max = 64) String word)
			throws Exception {
		return ResponseEntity.ok(dictionaryService.createWordByGemini(word));
	}

	@PostMapping
	public ResponseEntity<?> createWord(@Valid @RequestBody DictionaryEntryDTO dictionaryEntryDTO) {
		return ResponseEntity.status(201).body(dictionaryService.createWord(dictionaryEntryDTO));
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> updateWord(@PathVariable Long id,
			@Valid @RequestBody DictionaryEntryDTO dictionaryEntryDTO) {
		return ResponseEntity.ok(dictionaryService.updateWord(id, dictionaryEntryDTO));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteWord(@PathVariable Long id) {
		dictionaryService.deleteWord(id);
		return ResponseEntity.noContent().build();
	}
}
