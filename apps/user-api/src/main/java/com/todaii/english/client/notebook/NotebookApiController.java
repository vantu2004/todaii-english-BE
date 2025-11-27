package com.todaii.english.client.notebook;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.todaii.english.client.security.CustomUserDetails;
import com.todaii.english.core.entity.NotebookItem;
import com.todaii.english.shared.request.client.NotebookRequest;
import com.todaii.english.shared.response.NotebookNode;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notebook")
public class NotebookApiController {

	private final NotebookService notebookService;

	private Long currentUserId(Authentication authentication) {
		CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
		return principal.getUser().getId();
	}

	@GetMapping
	public ResponseEntity<List<NotebookNode>> getTree(Authentication authentication) {
		return ResponseEntity.ok(notebookService.getTree(currentUserId(authentication)));
	}

	@PostMapping
	public ResponseEntity<NotebookItem> create(Authentication authentication,
			@Valid @RequestBody NotebookRequest notebookRequest) {
		return ResponseEntity.status(201)
				.body(notebookService.createItem(currentUserId(authentication), notebookRequest));
	}

	@PutMapping("/{id}")
	public ResponseEntity<NotebookItem> rename(Authentication authentication, @PathVariable Long id,
			@RequestParam String name) {
		return ResponseEntity.ok(notebookService.rename(currentUserId(authentication), id, name));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(Authentication authentication, @PathVariable Long id) {
		notebookService.deleteItem(currentUserId(authentication), id);
		return ResponseEntity.ok().build();
	}
}
