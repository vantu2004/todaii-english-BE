package com.todaii.english.server.article;

import java.util.List;

import org.hibernate.validator.constraints.Length;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.todaii.english.core.entity.ArticleParagraph;
import com.todaii.english.shared.request.server.ArticleParagraphRequest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/article")
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

	@PostMapping("/paragraph/translate")
	public ResponseEntity<String> translateParagraph(@RequestParam @NotNull @Length(min = 10) String textEn) {
		String translated = articleParagraphService.translateParagraph(textEn);
		return ResponseEntity.ok(translated);
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
