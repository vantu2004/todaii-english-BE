package com.todaii.english.server.vocabulary;

import java.util.List;

import org.hibernate.validator.constraints.Length;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
import com.todaii.english.shared.response.PagedResponse;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/vocab-group")
public class VocabGroupApiController {
	private final VocabGroupService vocabGroupService;

	@GetMapping("/no-paged")
	public ResponseEntity<List<VocabGroup>> getAllNoPaged() {
		return ResponseEntity.ok(vocabGroupService.findAll());
	}

	@GetMapping
	public ResponseEntity<PagedResponse<VocabGroup>> getAllPaged(
			@RequestParam(defaultValue = "1") @Min(value = 1, message = "Page must be at least 1") int page,
			@RequestParam(defaultValue = "10") @Min(value = 1, message = "Size must be at least 1") int size,
			@RequestParam(defaultValue = "id") String sortBy, @RequestParam(defaultValue = "desc") String direction,
			@RequestParam(required = false) String keyword) {

		Page<VocabGroup> groups = vocabGroupService.findAllPaged(page, size, sortBy, direction, keyword);

		PagedResponse<VocabGroup> response = new PagedResponse<>(groups.getContent(), page, size,
				groups.getTotalElements(), groups.getTotalPages(), groups.isFirst(), groups.isLast(), sortBy,
				direction);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/{id}")
	public ResponseEntity<VocabGroup> getById(@PathVariable Long id) {
		return ResponseEntity.ok(vocabGroupService.findById(id));
	}

	@PostMapping
	public ResponseEntity<VocabGroup> create(
			@RequestParam @NotBlank(message = "Name must not be blank") @Length(max = 191, message = "Name cannot exceed 191 characters") String name) {
		return ResponseEntity.status(201).body(vocabGroupService.create(name));
	}

	@PutMapping("/{id}")
	public ResponseEntity<VocabGroup> update(@PathVariable Long id,
			@RequestParam @NotBlank(message = "Name must not be blank") @Length(max = 191, message = "Name cannot exceed 191 characters") String name) {
		return ResponseEntity.ok(vocabGroupService.update(id, name));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		vocabGroupService.softDelete(id);
		return ResponseEntity.ok().build();
	}

	@PatchMapping("/{id}/enabled")
	public ResponseEntity<Void> toggleEnabled(@PathVariable Long id) {
		vocabGroupService.toggleEnabled(id);
		return ResponseEntity.ok().build();
	}
}
