package com.todaii.english.client.vocabulary;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.todaii.english.core.entity.VocabGroup;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/vocab-group")
public class VocabGroupApiController {
	private final VocabGroupService vocabGroupService;

	@GetMapping
	public ResponseEntity<List<VocabGroup>> getAllVocabGroups() {
		return ResponseEntity.ok(vocabGroupService.findAll());
	}
}
