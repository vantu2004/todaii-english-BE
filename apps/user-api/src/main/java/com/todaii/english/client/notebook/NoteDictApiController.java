package com.todaii.english.client.notebook;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.todaii.english.client.UserUtils;
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

	@GetMapping("/{noteId}/words")
	public ResponseEntity<List<DictionaryEntry>> getWords(Authentication authentication, @PathVariable Long noteId) {
		return ResponseEntity.ok(noteDictService.getEntries(UserUtils.getCurrentAdminId(authentication), noteId));
	}

	@PutMapping("/{noteId}/word/{entryId}")
	public ResponseEntity<Void> addWord(Authentication authentication, @PathVariable Long noteId,
			@PathVariable Long entryId) {
		noteDictService.addEntry(UserUtils.getCurrentAdminId(authentication), noteId, entryId);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{noteId}/word/{entryId}")
	public ResponseEntity<Void> removeWord(Authentication authentication, @PathVariable Long noteId,
			@PathVariable Long entryId) {
		noteDictService.removeEntry(UserUtils.getCurrentAdminId(authentication), noteId, entryId);
		return ResponseEntity.ok().build();
	}
}
