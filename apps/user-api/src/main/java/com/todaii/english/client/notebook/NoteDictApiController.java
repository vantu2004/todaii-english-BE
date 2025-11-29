package com.todaii.english.client.notebook;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.todaii.english.client.security.CustomUserDetails;
import com.todaii.english.core.entity.DictionaryEntry;

import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notebook")
@Tag(name = "Notebook Dictionary", description = "APIs for managing dictionary entries in user notebooks")
public class NoteDictApiController {

	private final NoteDictService noteDictService;

	private Long userId(Authentication authentication) {
		CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
		return principal.getUser().getId();
	}

	@GetMapping("/{noteId}/words")
	@Operation(summary = "Get words in notebook", description = "Retrieve all dictionary entries in a specific notebook")
	public ResponseEntity<List<DictionaryEntry>> getWords(Authentication auth, @PathVariable Long noteId) {
		return ResponseEntity.ok(noteDictService.getEntries(userId(auth), noteId));
	}

	@PutMapping("/{noteId}/word/{entryId}")
	@Operation(summary = "Add word to notebook", description = "Add a dictionary entry to a specific notebook")
	public ResponseEntity<Void> addWord(Authentication auth, @PathVariable Long noteId, @PathVariable Long entryId) {
		noteDictService.addEntry(userId(auth), noteId, entryId);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{noteId}/word/{entryId}")
	@Operation(summary = "Remove word from notebook", description = "Remove a dictionary entry from a specific notebook")
	public ResponseEntity<Void> removeWord(Authentication auth, @PathVariable Long noteId, @PathVariable Long entryId) {
		noteDictService.removeEntry(userId(auth), noteId, entryId);
		return ResponseEntity.ok().build();
	}
}
