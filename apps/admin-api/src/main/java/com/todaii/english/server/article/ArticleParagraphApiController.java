package com.todaii.english.server.article;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.todaii.english.core.entity.ArticleParagraph;
import com.todaii.english.shared.request.server.ArticleParagraphRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/article")
@Validated
public class ArticleParagraphApiController {
	private final ArticleParagraphService articleParagraphService;

	@GetMapping("/{articleId}/paragraph")
	public ResponseEntity<List<ArticleParagraph>> getByArticleId(@PathVariable Long articleId) {
		List<ArticleParagraph> list = articleParagraphService.getByArticleId(articleId);
		if (list.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(list);
	}

	@PostMapping("/{articleId}/paragraph")
	public ResponseEntity<ArticleParagraph> create(@PathVariable Long articleId,
			@Valid @RequestBody ArticleParagraphRequest request) {
		ArticleParagraph paragraph = articleParagraphService.create(articleId, request);
		return ResponseEntity.status(201).body(paragraph);
	}

	@PutMapping("/paragraph/{paragraphId}")
	public ResponseEntity<ArticleParagraph> update(@PathVariable Long paragraphId,
			@Valid @RequestBody ArticleParagraphRequest request) {
		return ResponseEntity.ok(articleParagraphService.update(paragraphId, request));
	}

	@DeleteMapping("/paragraph/{paragraphId}")
	public ResponseEntity<Void> delete(@PathVariable Long paragraphId) {
		articleParagraphService.delete(paragraphId);
		return ResponseEntity.noContent().build();
	}
}
