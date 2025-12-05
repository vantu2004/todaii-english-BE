package com.todaii.english.server.article;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.todaii.english.core.entity.ArticleParagraph;
import com.todaii.english.server.AdminUtils;
import com.todaii.english.shared.exceptions.BusinessException;
import com.todaii.english.shared.request.server.ArticleParagraphRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/article")
@Tag(
	    name = "Article Paragraph",
	    description = "Operations related to article paragraphs, including creating, translating, listing and deleting paragraphs"
	)
public class ArticleParagraphApiController {

	private final ArticleParagraphService articleParagraphService;

	// =============================
	// GET BY ARTICLE ID
	// =============================

	@Operation(
		summary = "Get all paragraphs by Article ID",
		description = "Returns a list of paragraphs belonging to the selected article",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "List of paragraphs",
				content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = ArticleParagraph.class),
					examples = @ExampleObject(
						value = """
						[
						  {
						    "id": 1,
						    "paraOrder": 1,
						    "textEn": "This is the first paragraph.",
						    "textVi": "Đây là đoạn đầu tiên.",
						    "article": null
						  },
						  {
						    "id": 2,
						    "paraOrder": 2,
						    "textEn": "This is the second paragraph.",
						    "textVi": "Đây là đoạn thứ hai.",
						    "article": null
						  }
						]
						"""
					)
				)
			),
			@ApiResponse(responseCode = "204", description = "No data found")
		}
	)
	@GetMapping("/{articleId}/paragraph")
	public ResponseEntity<List<ArticleParagraph>> getByArticleId(
			@Parameter(description = "ID of article") @PathVariable Long articleId) {
		return ResponseEntity.ok(articleParagraphService.getByArticleId(articleId));
	}

	// =============================
	// CREATE / SAVE PARAGRAPH
	// =============================

	@Operation(
		summary = "Create a new paragraph",
		description = "Creates a new paragraph and attaches it to an article",
		requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
			required = true,
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = ArticleParagraphRequest.class),
				examples = @ExampleObject(
					value = """
					{
					  "paraOrder": 1,
					  "textEn": "Paragraph in English",
					  "textVi": "Đoạn văn tiếng Việt"
					}
					"""
				)
			)
		),
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "Created paragraph",
				content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = ArticleParagraph.class),
					examples = @ExampleObject(
						value = """
						{
						  "id": 10,
						  "paraOrder": 1,
						  "textEn": "Paragraph in English",
						  "textVi": "Đoạn văn tiếng Việt",
						  "article": null
						}
						"""
					)
				)
			)
		}
	)
	@PostMapping("/{articleId}/paragraph")
	public ResponseEntity<ArticleParagraph> save(
			@PathVariable Long articleId,
			@Valid @RequestBody ArticleParagraphRequest request) {

		return ResponseEntity.status(200).body(
				articleParagraphService.save(articleId, request));
	}

	// =============================
	// TRANSLATE PARAGRAPH
	// =============================

	@Operation(
		summary = "Translate an English paragraph to Vietnamese",
		description = "Translate raw text (minimum 10 characters)",
		requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
			required = true,
			content = @Content(
				mediaType = "application/json",
				examples = @ExampleObject(
					value = """
					{
					  "textEn": "This is a sample paragraph to translate."
					}
					"""
				)
			)
		),
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "Translated text",
				content = @Content(
					mediaType = "text/plain",
					examples = @ExampleObject(value = "Đây là đoạn văn mẫu để dịch.")
				)
			)
		}
	)
	@PostMapping("/paragraph/translate")
	public ResponseEntity<String> translateParagraph(Authentication authentication,
			@RequestBody Map<String, String> body) {
		String textEn = body.get("textEn");

		if (!StringUtils.hasText(textEn) || textEn.length() < 10) {
			throw new BusinessException(400, "Text to translate must be at least 10 characters");
		}

		String translated = articleParagraphService.translateParagraph(AdminUtils.getCurrentAdminId(authentication),
				textEn);
		return ResponseEntity.ok(translated);
	}

	@DeleteMapping("/paragraph/{paragraphId}")
	public ResponseEntity<Void> delete(@PathVariable Long paragraphId) {
		articleParagraphService.delete(paragraphId);
		return ResponseEntity.noContent().build();
	}
}
