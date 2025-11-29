package com.todaii.english.client.vocabulary;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.todaii.english.core.entity.VocabDeck;
import com.todaii.english.shared.enums.CefrLevel;
import com.todaii.english.shared.response.PagedResponse;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/vocab-deck")
@Tag(name = "Vocabulary Decks", description = "APIs for accessing vocabulary decks and filtering")
public class VocabDeckApiController {
	private final VocabDeckService vocabDeckService;

	@GetMapping
	@Operation(summary = "Get all vocabulary decks", description = "Retrieve a list of all vocabulary decks")
	public ResponseEntity<List<VocabDeck>> getAllVocabDecks() {
		return ResponseEntity.ok(vocabDeckService.findAll());
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get a vocabulary deck by ID", description = "Retrieve details of a specific vocabulary deck by its ID")
	public ResponseEntity<VocabDeck> getVocabDeckById(@PathVariable Long id) {
		return ResponseEntity.ok(vocabDeckService.findById(id));
	}

	@GetMapping("/filter")
	@Operation(summary = "Filter vocabulary decks", description = "Filter vocabulary decks by keyword, CEFR level, alias, and pagination")
	public ResponseEntity<PagedResponse<VocabDeck>> filter(
			@RequestParam(required = false) String keyword,
			@RequestParam(required = false) CefrLevel cefrLevel,
			@RequestParam(required = false) String alias,
			@RequestParam(defaultValue = "1") @Min(value = 1, message = "Page must be at least 1") int page,
			@RequestParam(defaultValue = "10") @Min(value = 1, message = "Size must be at least 1") int size,
			@RequestParam(defaultValue = "updatedAt") String sortBy,
			@RequestParam(defaultValue = "desc") String direction) {

		Page<VocabDeck> filterVocabDecks = vocabDeckService.filterVocabDecks(keyword, cefrLevel, alias, page, size,
				sortBy, direction);

		PagedResponse<VocabDeck> pagedResponse = new PagedResponse<>(filterVocabDecks.getContent(), page, size,
				filterVocabDecks.getTotalElements(), filterVocabDecks.getTotalPages(), filterVocabDecks.isFirst(),
				filterVocabDecks.isLast(), sortBy, direction);

		return ResponseEntity.ok(pagedResponse);
	}
}
