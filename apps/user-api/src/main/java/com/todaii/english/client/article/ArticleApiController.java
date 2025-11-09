package com.todaii.english.client.article;

import java.time.LocalDate;
import java.util.List;

import org.hibernate.validator.constraints.Length;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.todaii.english.core.entity.Article;
import com.todaii.english.core.entity.DictionaryEntry;
import com.todaii.english.shared.response.PagedResponse;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/article")
public class ArticleApiController {
	private final ArticleService articleService;

	// lấy n articles gần đây nhất
	@GetMapping("/latest")
	public ResponseEntity<List<Article>> getLatestArticles(@RequestParam(defaultValue = "1") @Min(1) int size) {
		return ResponseEntity.ok(articleService.getLatestArticles(size));
	}

	// lấy articles theo ngày, định dạng YYYY-MM-DD (đúng format, số lượng ký tự)
	@GetMapping("/by-date/{date}")
	public ResponseEntity<PagedResponse<Article>> getArticlesByDate(@PathVariable String date,
			@RequestParam(defaultValue = "1") @Min(1) int page, @RequestParam(defaultValue = "10") @Min(1) int size,
			@RequestParam(defaultValue = "updatedAt") String sortBy,
			@RequestParam(defaultValue = "desc") String direction, @RequestParam(required = false) String keyword) {
		LocalDate parsedDate = LocalDate.parse(date);

		Page<Article> articles = articleService.getArticlesByDate(parsedDate, keyword, page, size, sortBy, direction);

		PagedResponse<Article> response = new PagedResponse<>(articles.getContent(), page, size,
				articles.getTotalElements(), articles.getTotalPages(), articles.isFirst(), articles.isLast(), sortBy,
				direction);

		return ResponseEntity.ok(response);
	}

	// lấy chi tiết article
	@GetMapping("/{id}")
	public ResponseEntity<Article> getArticleById(@PathVariable Long id) {
		return ResponseEntity.ok(articleService.findById(id));
	}

	// search article
	@GetMapping("/search")
	public ResponseEntity<PagedResponse<Article>> search(@RequestParam(defaultValue = "1") @Min(1) int page,
			@RequestParam(defaultValue = "10") @Min(1) int size,
			@RequestParam(defaultValue = "updatedAt") String sortBy,
			@RequestParam(defaultValue = "desc") String direction,
			@RequestParam(required = true) @NotBlank @Length(max = 1024) String keyword) {
		Page<Article> articles = articleService.search(keyword, page, size, sortBy, direction);

		PagedResponse<Article> response = new PagedResponse<>(articles.getContent(), page, size,
				articles.getTotalElements(), articles.getTotalPages(), articles.isFirst(), articles.isLast(), sortBy,
				direction);

		return ResponseEntity.ok(response);
	}

	/*
	 * lấy danh sách entries theo article đã phân trang, còn list entries mặc định
	 * của article dùng cho deck
	 */
	@GetMapping("/{id}/entry")
	public ResponseEntity<PagedResponse<DictionaryEntry>> getPagedVocabulary(@PathVariable Long id,
			@RequestParam(defaultValue = "1") @Min(1) int page, @RequestParam(defaultValue = "10") @Min(1) int size) {
		Page<DictionaryEntry> entries = articleService.getPagedVocabulary(id, page, size);

		PagedResponse<DictionaryEntry> response = new PagedResponse<>(entries.getContent(), page, size,
				entries.getTotalElements(), entries.getTotalPages(), entries.isFirst(), entries.isLast(), "headword",
				"asc");

		return ResponseEntity.ok(response);
	}

	// lấy n articles liên quan
	@GetMapping("/{id}/related")
	public ResponseEntity<List<Article>> getRelatedArticles(@PathVariable Long id,
			@RequestParam(defaultValue = "5") @Min(1) int limit) {
		List<Article> related = articleService.getRelatedArticles(id, limit);

		return ResponseEntity.ok(related);
	}
}
