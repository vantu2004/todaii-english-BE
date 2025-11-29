package com.todaii.english.client.article;

import java.time.LocalDate;
import java.util.List;

import org.hibernate.validator.constraints.Length;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.todaii.english.client.security.CustomUserDetails;
import com.todaii.english.core.entity.Article;
import com.todaii.english.core.entity.DictionaryEntry;
import com.todaii.english.core.entity.DictionaryEntry_;
import com.todaii.english.shared.enums.CefrLevel;
import com.todaii.english.shared.response.PagedResponse;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/article")
@Tag(name = "Articles", description = "APIs for managing and retrieving articles")
public class ArticleApiController {

	private static final String SORT_DIRECTION = "asc";

	private final ArticleService articleService;

	@GetMapping("/latest")
	@Operation(summary = "Get latest articles", description = "Fetches the latest N articles")
	public ResponseEntity<List<Article>> getLatestArticles(
			@RequestParam(defaultValue = "1") @Min(value = 1, message = "Size must be at least 1") int size) {
		return ResponseEntity.ok(articleService.getLatestArticles(size));
	}

	@GetMapping("/top")
	@Operation(summary = "Get top articles", description = "Fetches the top N most viewed articles")
	public ResponseEntity<List<Article>> getTopArticles(
			@RequestParam(defaultValue = "1") @Min(value = 1, message = "Size must be at least 1") int size) {
		return ResponseEntity.ok(articleService.getTopArticles(size));
	}

	@GetMapping("/by-date/{date}")
	@Operation(summary = "Get articles by date", description = "Fetches articles by a specific date (YYYY-MM-DD)")
	public ResponseEntity<PagedResponse<Article>> getArticlesByDate(
			@PathVariable @NotBlank(message = "Date must not be blank") String date,
			@RequestParam(defaultValue = "1") @Min(value = 1, message = "Page must be at least 1") int page,
			@RequestParam(defaultValue = "10") @Min(value = 1, message = "Size must be at least 1") int size,
			@RequestParam(defaultValue = "createdAt") String sortBy,
			@RequestParam(defaultValue = "desc") String direction,
			@RequestParam(required = false) String keyword) {

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

	@GetMapping("/{id}")
	@Operation(summary = "Get article details", description = "Fetches a single article by its ID")
	public ResponseEntity<Article> getArticleById(@PathVariable Long id) {
		return ResponseEntity.ok(articleService.findById(id));
	}

	@Deprecated
	@GetMapping("/search")
	@Operation(summary = "Search articles", description = "Searches articles by keyword")
	public ResponseEntity<PagedResponse<Article>> search(
			@RequestParam(defaultValue = "1") @Min(value = 1, message = "Page must be at least 1") int page,
			@RequestParam(defaultValue = "10") @Min(value = 1, message = "Size must be at least 1") int size,
			@RequestParam(defaultValue = "updatedAt") String sortBy,
			@RequestParam(defaultValue = "desc") String direction,
			@RequestParam(required = true) @NotBlank(message = "Keyword must not be blank") 
			@Length(max = 1024, message = "Keyword must not exceed 1024 characters") String keyword) {

		Page<Article> articles = articleService.search(keyword, page, size, sortBy, direction);

		return ResponseEntity.ok(new PagedResponse<>(articles.getContent(), page, size, articles.getTotalElements(),
				articles.getTotalPages(), articles.isFirst(), articles.isLast(), sortBy, direction));
	}

	@GetMapping("/{id}/entry")
	@Operation(summary = "Get paged dictionary entries for an article", description = "Fetches a paginated list of dictionary entries for a given article")
	public ResponseEntity<PagedResponse<DictionaryEntry>> getPagedVocabulary(@PathVariable Long id,
			@RequestParam(defaultValue = "1") @Min(value = 1, message = "Size must be at least 1") int page,
			@RequestParam(defaultValue = "10") @Min(value = 1, message = "Size must be at least 1") int size) {
		Page<DictionaryEntry> entries = articleService.getPagedVocabulary(id, page, size);

		PagedResponse<DictionaryEntry> response = new PagedResponse<>(entries.getContent(), page, size,
				entries.getTotalElements(), entries.getTotalPages(), entries.isFirst(), entries.isLast(),
				DictionaryEntry_.HEADWORD, SORT_DIRECTION);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/{id}/related")
	@Operation(summary = "Get related articles", description = "Fetches N articles related to the given article ID")
	public ResponseEntity<List<Article>> getRelatedArticles(@PathVariable Long id,
			@RequestParam(defaultValue = "5") @Min(1) int limit) {
		List<Article> related = articleService.getRelatedArticles(id, limit);
		return ResponseEntity.ok(related);
	}

	@GetMapping("/filter")
	@Operation(summary = "Filter articles", description = "Filter articles by keyword, source, CEFR level, minimum views, and alias")
	public ResponseEntity<PagedResponse<Article>> filterArticles(@RequestParam(required = false) String keyword,
			@RequestParam(required = false) String sourceName,
			@RequestParam(required = false) CefrLevel cefrLevel,
			@RequestParam(required = false) Integer minViews,
			@RequestParam(required = false) String alias,
			@RequestParam(defaultValue = "1") @Min(1) int page,
			@RequestParam(defaultValue = "10") @Min(1) int size,
			@RequestParam(defaultValue = "updatedAt") String sortBy,
			@RequestParam(defaultValue = "desc") String direction) {

		Page<Article> articles = articleService.filterArticles(keyword, sourceName, cefrLevel, minViews, alias, page,
				size, sortBy, direction);

		PagedResponse<Article> response = new PagedResponse<>(articles.getContent(), page, size,
				articles.getTotalElements(), articles.getTotalPages(), articles.isFirst(), articles.isLast(), sortBy,
				direction);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/saved")
	@Operation(summary = "Get saved articles for current user", description = "Fetches the articles saved by the authenticated user")
	public ResponseEntity<List<Article>> getSavedArticlesByUserId(Authentication authentication) {
		CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
		Long currentUserId = principal.getUser().getId();
		return ResponseEntity.ok(articleService.getSavedArticlesByUserId(currentUserId));
	}

	@GetMapping("/{articleId}/is-saved")
	@Operation(summary = "Check if article is saved by current user", description = "Returns true if the authenticated user has saved the article")
	public ResponseEntity<Boolean> isSavedByUser(Authentication authentication, @PathVariable Long articleId) {
		CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
		Long currentUserId = principal.getUser().getId();
		return ResponseEntity.ok(articleService.isSavedByUser(articleId, currentUserId));
	}

	@GetMapping("/source")
	@Operation(summary = "Get all source names", description = "Fetches a list of all article sources")
	public ResponseEntity<List<String>> getSources() {
		return ResponseEntity.ok(articleService.getAllSources());
	}

}
