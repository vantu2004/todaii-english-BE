package com.todaii.english.server.dictionary;

import java.util.List;

import org.hibernate.validator.constraints.Length;
import org.springframework.data.domain.Page;
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
import com.todaii.english.shared.response.DictionaryApiResponse;
import com.todaii.english.shared.response.PagedResponse;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/dictionary")
public class DictionaryEntryApiController {
	private final DictionaryEntryService dictionaryService;

	@GetMapping("/raw-word")
	public ResponseEntity<DictionaryApiResponse[]> getRawWord(@RequestParam String word) {
		DictionaryApiResponse[] dictionaryApiResponses = dictionaryService.lookupWord(word);
		return ResponseEntity.ok(dictionaryApiResponses);
	}

	@Deprecated
	public ResponseEntity<List<DictionaryEntry>> getAllWords() {
		List<DictionaryEntry> dictionaryEntries = dictionaryService.findAll();
		if (dictionaryEntries.isEmpty()) {
			return ResponseEntity.noContent().build();
		}

		return ResponseEntity.ok(dictionaryEntries);
	}

	@GetMapping
	public ResponseEntity<PagedResponse<DictionaryEntry>> getAllPaged(
			@RequestParam(defaultValue = "1") @Min(value = 1, message = "Page must be at least 1") int page,
			@RequestParam(defaultValue = "20") @Min(value = 1, message = "Size must be at least 1") int size,
			@RequestParam(defaultValue = "headword") String sortBy,
			@RequestParam(defaultValue = "asc") String direction, @RequestParam(required = false) String keyword) {

		Page<DictionaryEntry> entries = dictionaryService.findAllPaged(page, size, sortBy, direction, keyword);

		PagedResponse<DictionaryEntry> response = new PagedResponse<>(entries.getContent(), page, size,
				entries.getTotalElements(), entries.getTotalPages(), entries.isFirst(), entries.isLast(), sortBy,
				direction);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/{id}")
	public ResponseEntity<DictionaryEntry> getWord(@PathVariable Long id) {
		return ResponseEntity.ok(dictionaryService.findById(id));
	}

	@PostMapping("/gemini")
	public ResponseEntity<List<DictionaryEntry>> createWordByGemini(
			@RequestParam @NotBlank @Length(max = 64) String word) throws Exception {
		return ResponseEntity.ok(dictionaryService.createWordByGemini(word));
	}

	@PostMapping
	public ResponseEntity<DictionaryEntry> createWord(@Valid @RequestBody DictionaryEntryDTO dictionaryEntryDTO) {
		return ResponseEntity.status(201).body(dictionaryService.createWord(dictionaryEntryDTO));
	}

	@PutMapping("/{id}")
	public ResponseEntity<DictionaryEntry> updateWord(@PathVariable Long id,
			@Valid @RequestBody DictionaryEntryDTO dictionaryEntryDTO) {
		return ResponseEntity.ok(dictionaryService.updateWord(id, dictionaryEntryDTO));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteWord(@PathVariable Long id) {
		dictionaryService.deleteWord(id);
		return ResponseEntity.noContent().build();
	}
}
