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

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/vocab-deck")
public class VocabDeckApiController {
	private final VocabDeckService vocabDeckService;

	@GetMapping
	public ResponseEntity<List<VocabDeck>> getAllVocabDecks() {
		return ResponseEntity.ok(vocabDeckService.findAll());
	}

	@GetMapping("/{id}")
	public ResponseEntity<VocabDeck> getVocabDeckById(@PathVariable Long id) {
		return ResponseEntity.ok(vocabDeckService.findById(id));
	}

	@GetMapping("/filter")
	public ResponseEntity<PagedResponse<VocabDeck>> filter(@RequestParam(required = false) String keyword,
			@RequestParam(required = false) CefrLevel cefrLevel, @RequestParam(required = false) String alias,
			@RequestParam(defaultValue = "1") @Min(value = 1, message = "Size must be at least 1") int page,
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
