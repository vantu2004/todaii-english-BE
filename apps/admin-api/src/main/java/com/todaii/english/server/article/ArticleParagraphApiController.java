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
	public ResponseEntity<ArticleParagraph> save(@PathVariable Long articleId,
			@Valid @RequestBody ArticleParagraphRequest request) {
		ArticleParagraph paragraph = articleParagraphService.save(articleId, request);
		return ResponseEntity.status(200).body(paragraph);
	}

	@PostMapping("/paragraph/translate")
	public ResponseEntity<String> translateParagraph(
			@RequestBody @NotNull(message = "Text to translate must not be null") @Length(min = 10, message = "Text to translate must be at least 10 characters") String textEn) {

		String translated = articleParagraphService.translateParagraph(textEn);
		return ResponseEntity.ok(translated);
	}

	@DeleteMapping("/paragraph/{paragraphId}")
	public ResponseEntity<Void> delete(@PathVariable Long paragraphId) {
		articleParagraphService.delete(paragraphId);
		return ResponseEntity.noContent().build();
	}
}
