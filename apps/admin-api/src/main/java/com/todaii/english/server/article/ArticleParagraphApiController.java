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

import jakarta.validation.Valid;
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

	/*
	 * @RequestBody làm Spring không parse field textEn vào string raw - thay vào
	 * đó, textEn ở method sẽ nhận toàn bộ JSON object dưới dạng chuỗi:
	 * {"textEn":""}
	 */
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
