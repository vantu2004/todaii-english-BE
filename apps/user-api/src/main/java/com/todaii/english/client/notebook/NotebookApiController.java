package com.todaii.english.client.notebook;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.todaii.english.client.UserUtils;
import com.todaii.english.core.entity.NotebookItem;
import com.todaii.english.shared.request.client.NotebookRequest;
import com.todaii.english.shared.response.NotebookNode;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notebook")
@Tag(name = "Notebook", description = "APIs for managing user notebook items")
public class NotebookApiController {
	private final NotebookService notebookService;

	@GetMapping
	@Operation(summary = "Get notebook tree", description = "Retrieve the full notebook tree for the current user")
	public ResponseEntity<List<NotebookNode>> getTree(Authentication authentication) {
		return ResponseEntity.ok(notebookService.getTree(UserUtils.getCurrentAdminId(authentication)));
	}

	@PostMapping
	@Operation(summary = "Create notebook item", description = "Create a new notebook item for the current user")
	public ResponseEntity<NotebookItem> create(Authentication authentication,
			@Valid @RequestBody NotebookRequest notebookRequest) {
		return ResponseEntity.status(201)
				.body(notebookService.createItem(UserUtils.getCurrentAdminId(authentication), notebookRequest));
	}

	@PutMapping("/{id}")
	@Operation(summary = "Rename notebook item", description = "Rename an existing notebook item for the current user")
	public ResponseEntity<NotebookItem> rename(Authentication authentication, @PathVariable Long id,
			@RequestParam String name) {
		return ResponseEntity.ok(notebookService.rename(UserUtils.getCurrentAdminId(authentication), id, name));
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Delete notebook item", description = "Delete an existing notebook item for the current user")
	public ResponseEntity<Void> delete(Authentication authentication, @PathVariable Long id) {
		notebookService.deleteItem(UserUtils.getCurrentAdminId(authentication), id);
		return ResponseEntity.ok().build();
	}
}
