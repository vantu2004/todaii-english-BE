package com.todaii.english.client.notebook;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.todaii.english.client.security.CustomUserDetails;
import com.todaii.english.core.entity.DictionaryEntry;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notebook")
public class NoteDictApiController {

	private final NoteDictService noteDictService;

	private Long userId(Authentication authentication) {
		CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
		return principal.getUser().getId();
	}

	@GetMapping("/{noteId}/words")
	public ResponseEntity<List<DictionaryEntry>> getWords(Authentication auth, @PathVariable Long noteId) {
		return ResponseEntity.ok(noteDictService.getEntries(userId(auth), noteId));
	}

	@PutMapping("/{noteId}/word/{entryId}")
	public ResponseEntity<Void> addWord(Authentication auth, @PathVariable Long noteId, @PathVariable Long entryId) {
		noteDictService.addEntry(userId(auth), noteId, entryId);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{noteId}/word/{entryId}")
	public ResponseEntity<Void> removeWord(Authentication auth, @PathVariable Long noteId, @PathVariable Long entryId) {
		noteDictService.removeEntry(userId(auth), noteId, entryId);
		return ResponseEntity.ok().build();
	}
}
