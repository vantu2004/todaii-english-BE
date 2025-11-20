package com.todaii.english.client.article;

import java.time.LocalDate;
import java.util.List;

import org.hibernate.validator.constraints.Length;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.todaii.english.client.security.CustomUserDetails;
import com.todaii.english.core.entity.Article;
import com.todaii.english.core.entity.DictionaryEntry;
import com.todaii.english.core.entity.DictionaryEntry_;
import com.todaii.english.shared.enums.CefrLevel;
import com.todaii.english.shared.response.PagedResponse;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/article")
public class ArticleApiController {
	private static final String SORT_DIRECTION = "asc";

	private final ArticleService articleService;

	// lấy n articles gần đây nhất
	@GetMapping("/latest")
	public ResponseEntity<List<Article>> getLatestArticles(
			@RequestParam(defaultValue = "1") @Min(value = 1, message = "Size must be at least 1") int size) {
		return ResponseEntity.ok(articleService.getLatestArticles(size));
	}

	// lấy n article được xem nhiều nhất
	@GetMapping("/top")
	public ResponseEntity<List<Article>> getTopArticles(
			@RequestParam(defaultValue = "1") @Min(value = 1, message = "Size must be at least 1") int size) {
		return ResponseEntity.ok(articleService.getTopArticles(size));
	}

	// lấy articles theo ngày, định dạng YYYY-MM-DD (đúng format, số lượng ký tự)
	@GetMapping("/by-date/{date}")
	public ResponseEntity<PagedResponse<Article>> getArticlesByDate(
			@PathVariable @NotBlank(message = "Date must not be blank") String date,
			@RequestParam(defaultValue = "1") @Min(value = 1, message = "Page must be at least 1") int page,
			@RequestParam(defaultValue = "10") @Min(value = 1, message = "Size must be at least 1") int size,
			@RequestParam(defaultValue = "updatedAt") String sortBy,
			@RequestParam(defaultValue = "desc") String direction, @RequestParam(required = false) String keyword) {

		LocalDate parsedDate;
		try {
			parsedDate = LocalDate.parse(date);
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid date format. Expected format is YYYY-MM-DD");
		}

		Page<Article> articles = articleService.getArticlesByDate(parsedDate, keyword, page, size, sortBy, direction);

		return ResponseEntity.ok(new PagedResponse<>(articles.getContent(), page, size, articles.getTotalElements(),
				articles.getTotalPages(), articles.isFirst(), articles.isLast(), sortBy, direction));
	}

	// lấy chi tiết article
	@GetMapping("/{id}")
	public ResponseEntity<Article> getArticleById(@PathVariable Long id) {
		return ResponseEntity.ok(articleService.findById(id));
	}

	// search article
	@GetMapping("/search")
	public ResponseEntity<PagedResponse<Article>> search(
			@RequestParam(defaultValue = "1") @Min(value = 1, message = "Page must be at least 1") int page,
			@RequestParam(defaultValue = "10") @Min(value = 1, message = "Size must be at least 1") int size,
			@RequestParam(defaultValue = "updatedAt") String sortBy,
			@RequestParam(defaultValue = "desc") String direction,
			@RequestParam(required = true) @NotBlank(message = "Keyword must not be blank") @Length(max = 1024, message = "Keyword must not exceed 1024 characters") String keyword) {

		Page<Article> articles = articleService.search(keyword, page, size, sortBy, direction);

		return ResponseEntity.ok(new PagedResponse<>(articles.getContent(), page, size, articles.getTotalElements(),
				articles.getTotalPages(), articles.isFirst(), articles.isLast(), sortBy, direction));
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
				entries.getTotalElements(), entries.getTotalPages(), entries.isFirst(), entries.isLast(),
				DictionaryEntry_.HEADWORD, SORT_DIRECTION);

		return ResponseEntity.ok(response);
	}

	// lấy n articles liên quan
	@GetMapping("/{id}/related")
	public ResponseEntity<List<Article>> getRelatedArticles(@PathVariable Long id,
			@RequestParam(defaultValue = "5") @Min(1) int limit) {
		List<Article> related = articleService.getRelatedArticles(id, limit);

		return ResponseEntity.ok(related);
	}

	// filter với nhiều thuộc tính (sourceName, cefrlevel, view, topic)
	@GetMapping("/filter")
	public ResponseEntity<PagedResponse<Article>> filterArticles(@RequestParam(required = false) String keyword,
			@RequestParam(required = false) String sourceName, @RequestParam(required = false) CefrLevel cefrLevel,
			@RequestParam(required = false) Integer minViews, @RequestParam(required = false) Long topicId,
			@RequestParam(defaultValue = "1") @Min(1) int page, @RequestParam(defaultValue = "10") @Min(1) int size,
			@RequestParam(defaultValue = "updatedAt") String sortBy,
			@RequestParam(defaultValue = "desc") String direction) {
		Page<Article> articles = articleService.filterArticles(keyword, sourceName, cefrLevel, minViews, topicId, page,
				size, sortBy, direction);

		PagedResponse<Article> response = new PagedResponse<>(articles.getContent(), page, size,
				articles.getTotalElements(), articles.getTotalPages(), articles.isFirst(), articles.isLast(), sortBy,
				direction);

		return ResponseEntity.ok(response);
	}

	// lấy danh sách articles đc lưu bỏi user
	@GetMapping("/saved")
	public ResponseEntity<List<Article>> getSavedArticlesByUserId(Authentication authentication) {
		CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
		Long currentUserId = principal.getUser().getId();

		return ResponseEntity.ok(articleService.getSavedArticlesByUserId(currentUserId));
	}

	// check article có đc lưu bởi user ko
	@GetMapping("/{articleId}/is-saved")
	public ResponseEntity<Boolean> isSavedByUser(Authentication authentication, @PathVariable Long articleId) {
		CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
		Long currentUserId = principal.getUser().getId();

		return ResponseEntity.ok(articleService.isSavedByUser(articleId, currentUserId));
	}
}
