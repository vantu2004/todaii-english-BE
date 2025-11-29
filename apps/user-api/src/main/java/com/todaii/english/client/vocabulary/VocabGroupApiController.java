package com.todaii.english.client.vocabulary;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.todaii.english.core.entity.VocabGroup;

import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/vocab-group")
@Tag(name = "Vocabulary Groups", description = "APIs for retrieving vocabulary groups")
public class VocabGroupApiController {
	private final VocabGroupService vocabGroupService;

	@GetMapping
	@Operation(summary = "Get all vocabulary groups", description = "Retrieve a list of all vocabulary groups")
	public ResponseEntity<List<VocabGroup>> getAllVocabGroups() {
		return ResponseEntity.ok(vocabGroupService.findAll());
	}
}
