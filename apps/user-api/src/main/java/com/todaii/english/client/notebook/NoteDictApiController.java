package com.todaii.english.client.notebook;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.todaii.english.core.entity.DictionaryEntry;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notebook")
public class NoteDictApiController {
	private final NoteDictService noteDictService;

	@GetMapping("/{noteId}/words")
	public ResponseEntity<List<DictionaryEntry>> getWords(@PathVariable Long noteId) {
		return ResponseEntity.ok(noteDictService.getEntries(noteId));
	}

	@PutMapping("/{noteId}/word/{entryId}")
	public ResponseEntity<Void> addWordToNote(@PathVariable Long noteId, @PathVariable Long entryId) {
		noteDictService.addEntry(noteId, entryId);

		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{noteId}/word/{entryId}")
	public ResponseEntity<Void> removeWordFromNote(@PathVariable Long noteId, @PathVariable Long entryId) {
		noteDictService.removeEntry(noteId, entryId);

		return ResponseEntity.ok().build();
	}

}
