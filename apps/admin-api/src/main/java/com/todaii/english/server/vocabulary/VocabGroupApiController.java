package com.todaii.english.server.vocabulary;

import java.util.List;

import org.hibernate.validator.constraints.Length;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.todaii.english.core.entity.VocabGroup;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/vocab-group")
public class VocabGroupApiController {
	private final VocabGroupService vocabGroupService;

	@GetMapping
	public ResponseEntity<?> getAll() {
		List<VocabGroup> vocabGroups = vocabGroupService.findAll();
		if (vocabGroups.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(vocabGroups);
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getById(@PathVariable Long id) {
		return ResponseEntity.ok(vocabGroupService.findById(id));
	}

	@PostMapping
	public ResponseEntity<?> create(@RequestParam @NotBlank @Length(max = 191) String name) {
		return ResponseEntity.status(201).body(vocabGroupService.create(name));
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> update(@PathVariable Long id, @RequestParam @NotBlank @Length(max = 191) String name) {
		return ResponseEntity.ok(vocabGroupService.update(id, name));
	}

	@DeleteMapping("/{id}")
	public void delete(@PathVariable Long id) {
		vocabGroupService.softDelete(id);
	}

	@PatchMapping("/{id}/enabled")
	public void toggleEnabled(@PathVariable Long id) {
		vocabGroupService.toggleEnabled(id);
	}
}
