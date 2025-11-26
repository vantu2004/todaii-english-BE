package com.todaii.english.client.notebook;

import java.util.List;

import org.hibernate.validator.constraints.Length;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.todaii.english.core.entity.NotebookItem;
import com.todaii.english.shared.request.client.NotebookRequest;
import com.todaii.english.shared.response.NotebookNode;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/notebook")
public class NotebookApiController {
	private final NotebookService notebookService;

	@GetMapping
	public ResponseEntity<List<NotebookNode>> getTree() {
		return ResponseEntity.ok(notebookService.getTree());
	}

	@PostMapping
	public ResponseEntity<NotebookItem> create(@Valid @RequestBody NotebookRequest notebookRequest) {
		return ResponseEntity.status(201).body(notebookService.createItem(notebookRequest.getName(),
				notebookRequest.getType(), notebookRequest.getParentId()));
	}

	@PutMapping("/{id}")
	public ResponseEntity<NotebookItem> rename(@PathVariable Long id,
			@NotBlank(message = "Name cannot be blank") @Length(max = 191, message = "Name must not exceed 191 characters") String name) {
		return ResponseEntity.ok(notebookService.rename(id, name));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		notebookService.deleteItem(id);

		return ResponseEntity.ok().build();
	}
}
