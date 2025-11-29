package com.todaii.english.server.vocabulary;

import java.util.List;

import org.hibernate.validator.constraints.Length;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.todaii.english.core.entity.VocabGroup;
import com.todaii.english.shared.response.PagedResponse;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/vocab-group")
@Tag(name = "Vocabulary Groups", description = "APIs for managing vocabulary groups")
public class VocabGroupApiController {

	private final VocabGroupService vocabGroupService;

	@GetMapping("/no-paged")
	@Operation(summary = "Get all vocabulary groups (no pagination)", description = "Returns a list of all vocabulary groups without pagination")
	@ApiResponse(responseCode = "200", description = "Groups fetched successfully")
	public ResponseEntity<List<VocabGroup>> getAllNoPaged() {
		return ResponseEntity.ok(vocabGroupService.findAll());
	}

	@GetMapping
	@Operation(summary = "Get paged list of vocabulary groups", description = "Returns vocabulary groups with pagination, sorting, and optional keyword filter")
	@ApiResponse(responseCode = "200", description = "Paged groups returned successfully")
	public ResponseEntity<PagedResponse<VocabGroup>> getAllPaged(
			@RequestParam(defaultValue = "1") @Min(value = 1, message = "Page must be at least 1") int page,
			@RequestParam(defaultValue = "10") @Min(value = 1, message = "Size must be at least 1") int size,
			@RequestParam(defaultValue = "id") String sortBy,
			@RequestParam(defaultValue = "desc") String direction,
			@RequestParam(required = false) String keyword) {

		Page<VocabGroup> groups = vocabGroupService.findAllPaged(page, size, sortBy, direction, keyword);

		PagedResponse<VocabGroup> response = new PagedResponse<>(groups.getContent(), page, size,
				groups.getTotalElements(), groups.getTotalPages(), groups.isFirst(), groups.isLast(), sortBy,
				direction);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get a vocabulary group by ID", description = "Returns a single vocabulary group by its ID")
	@ApiResponse(responseCode = "200", description = "Group fetched successfully")
	public ResponseEntity<VocabGroup> getById(@PathVariable Long id) {
		return ResponseEntity.ok(vocabGroupService.findById(id));
	}

	@PostMapping
	@Operation(summary = "Create a new vocabulary group", description = "Creates a new vocabulary group with the specified name")
	@ApiResponse(responseCode = "201", description = "Group created successfully")
	public ResponseEntity<VocabGroup> create(
			@RequestParam @NotBlank(message = "Name must not be blank") @Length(max = 191, message = "Name cannot exceed 191 characters") String name) {
		return ResponseEntity.status(201).body(vocabGroupService.create(name));
	}

	@PutMapping("/{id}")
	@Operation(summary = "Update a vocabulary group", description = "Updates the name of an existing vocabulary group")
	@ApiResponse(responseCode = "200", description = "Group updated successfully")
	public ResponseEntity<VocabGroup> update(@PathVariable Long id,
			@RequestParam @NotBlank(message = "Name must not be blank") @Length(max = 191, message = "Name cannot exceed 191 characters") String name) {
		return ResponseEntity.ok(vocabGroupService.update(id, name));
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Soft-delete a vocabulary group", description = "Marks a vocabulary group as deleted without permanently removing it")
	@ApiResponse(responseCode = "200", description = "Group deleted successfully")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		vocabGroupService.softDelete(id);
		return ResponseEntity.ok().build();
	}

	@PatchMapping("/{id}/enabled")
	@Operation(summary = "Toggle vocabulary group enabled status", description = "Enable or disable a vocabulary group")
	@ApiResponse(responseCode = "200", description = "Group status toggled successfully")
	public ResponseEntity<Void> toggleEnabled(@PathVariable Long id) {
		vocabGroupService.toggleEnabled(id);
		return ResponseEntity.ok().build();
	}
}
