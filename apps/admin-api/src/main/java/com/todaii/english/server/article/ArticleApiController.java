package com.todaii.english.server.article;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.todaii.english.core.entity.Article;
import com.todaii.english.shared.exceptions.BusinessException;
import com.todaii.english.shared.request.server.ArticleRequest;
import com.todaii.english.shared.response.NewsApiResponse;
import com.todaii.english.shared.response.PagedResponse;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/article")
public class ArticleApiController {
	private final ArticleService articleService;

	@Deprecated
	public ResponseEntity<List<Article>> getAllArticles() {
		List<Article> articles = articleService.findAll();
		if (articles.isEmpty()) {
			return ResponseEntity.noContent().build();
		}

		return ResponseEntity.ok(articles);
	}

	@GetMapping
	public ResponseEntity<PagedResponse<Article>> getAllPaged(
			@RequestParam(defaultValue = "1") @Min(value = 1, message = "Page must be at least 1") int page,
			@RequestParam(defaultValue = "10") @Min(value = 1, message = "Size must be at least 1") int size,
			@RequestParam(defaultValue = "id") String sortBy, @RequestParam(defaultValue = "desc") String direction,
			@RequestParam(required = false) String keyword) {

		Page<Article> articles = articleService.findAllPaged(page, size, sortBy, direction, keyword);

		PagedResponse<Article> response = new PagedResponse<>(articles.getContent(), page, size,
				articles.getTotalElements(), articles.getTotalPages(), articles.isFirst(), articles.isLast(), sortBy,
				direction);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/topic/{topicId}")
	public ResponseEntity<PagedResponse<Article>> getArticlesBytopicId(@PathVariable Long topicId,
			@RequestParam(defaultValue = "1") @Min(value = 1, message = "Page must be at least 1") int page,
			@RequestParam(defaultValue = "10") @Min(value = 1, message = "Size must be at least 1") int size,
			@RequestParam(defaultValue = "id") String sortBy, @RequestParam(defaultValue = "desc") String direction,
			@RequestParam(required = false) String keyword) {

		Page<Article> articles = articleService.findByTopicId(topicId, page, size, sortBy, direction, keyword);

		PagedResponse<Article> response = new PagedResponse<>(articles.getContent(), page, size,
				articles.getTotalElements(), articles.getTotalPages(), articles.isFirst(), articles.isLast(), sortBy,
				direction);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Article> getArticleById(@PathVariable Long id) {
		return ResponseEntity.ok(articleService.findById(id));
	}

	@PostMapping("/news-api")
	public ResponseEntity<NewsApiResponse> fetchArticles(
			@RequestParam(defaultValue = "new") @NotBlank(message = "Query must not be blank") String query,
			@RequestParam(defaultValue = "10") @Min(value = 1, message = "Page size must be at least 1") @Max(value = 100, message = "Page size cannot exceed 100") int pageSize,
			@RequestParam(defaultValue = "1") @Min(value = 1, message = "Page must be at least 1") int page,
			@RequestParam(defaultValue = "publishedAt") String sortBy) {

		if (page * pageSize > 100) {
			throw new BusinessException(400, "Your account is limited to a maximum of 100 results");
		}

		NewsApiResponse newsApiResponse = articleService.fetchFromNewsApi(query, pageSize, page, sortBy);
		return ResponseEntity.ok(newsApiResponse);
	}

	@PostMapping
	public ResponseEntity<Article> createArticle(@Valid @RequestBody ArticleRequest articleRequest) {
		return ResponseEntity.status(201).body(articleService.create(articleRequest));
	}

	@PutMapping("/{id}")
	public ResponseEntity<Article> updateArticle(@PathVariable Long id,
			@Valid @RequestBody ArticleRequest articleRequest) {
		return ResponseEntity.ok(articleService.update(id, articleRequest));
	}

	@PatchMapping("/{id}/enabled")
	public ResponseEntity<Void> toggleEnabled(@PathVariable Long id) {
		articleService.toggleEnabled(id);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteArticle(@PathVariable Long id) {
		articleService.deleteById(id);
		return ResponseEntity.noContent().build();
	}
}
