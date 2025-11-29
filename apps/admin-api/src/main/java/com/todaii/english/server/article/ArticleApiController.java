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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/article")
@Tag(name = "Article", description = "API for managing articles, fetching from NewsAPI, and CRUD operations")
public class ArticleApiController {

	private final ArticleService articleService;

	// --------------------------------------------------
	// GET PAGED
	// --------------------------------------------------
	@GetMapping
	@Operation(
		summary = "Get paginated list of articles",
		description = "Returns a paginated list of Articles based on page, size, sort, and keyword."
	)
	@ApiResponse(responseCode = "200", description = "Successfully retrieved articles")
	public ResponseEntity<PagedResponse<Article>> getAllPaged(
			@RequestParam(defaultValue = "1") @Min(1) int page,
			@RequestParam(defaultValue = "10") @Min(1) int size,
			@RequestParam(defaultValue = "id") String sortBy,
			@RequestParam(defaultValue = "desc") String direction,
			@RequestParam(required = false) String keyword) {

		Page<Article> articles = articleService.findAllPaged(page, size, sortBy, direction, keyword);

		PagedResponse<Article> response = new PagedResponse<>(
			articles.getContent(), page, size, articles.getTotalElements(),
			articles.getTotalPages(), articles.isFirst(), articles.isLast(),
			sortBy, direction
		);

		return ResponseEntity.ok(response);
	}

	// --------------------------------------------------
	// GET BY TOPIC
	// --------------------------------------------------
	@GetMapping("/topic/{topicId}")
	@Operation(
		summary = "Get articles by Topic ID",
		description = "Returns a paginated list of Articles for a specific Topic."
	)
	@ApiResponse(responseCode = "200", description = "Successfully retrieved articles for the topic")
	public ResponseEntity<PagedResponse<Article>> getArticlesByTopicId(
			@PathVariable Long topicId,
			@RequestParam(defaultValue = "1") @Min(1) int page,
			@RequestParam(defaultValue = "10") @Min(1) int size,
			@RequestParam(defaultValue = "id") String sortBy,
			@RequestParam(defaultValue = "desc") String direction,
			@RequestParam(required = false) String keyword) {

		Page<Article> articles = articleService.findByTopicId(topicId, page, size, sortBy, direction, keyword);

		PagedResponse<Article> response = new PagedResponse<>(
			articles.getContent(), page, size, articles.getTotalElements(),
			articles.getTotalPages(), articles.isFirst(), articles.isLast(),
			sortBy, direction
		);

		return ResponseEntity.ok(response);
	}

	// --------------------------------------------------
	// GET BY ID
	// --------------------------------------------------
	@GetMapping("/{id}")
	@Operation(summary = "Get article details", description = "Retrieve full information of an Article by its ID.")
	@ApiResponse(responseCode = "200", description = "Successfully retrieved article")
	@ApiResponse(responseCode = "404", description = "Article not found")
	public ResponseEntity<Article> getArticleById(@PathVariable Long id) {
		return ResponseEntity.ok(articleService.findById(id));
	}

	// --------------------------------------------------
	// FETCH FROM NEWS API
	// --------------------------------------------------
	@PostMapping("/news-api")
	@Operation(
		summary = "Fetch articles from NewsAPI",
		description = "Fetch articles from NewsAPI based on query and pagination parameters.",
		parameters = {
			@io.swagger.v3.oas.annotations.Parameter(name = "query", example = "technology"),
			@io.swagger.v3.oas.annotations.Parameter(name = "pageSize", example = "10"),
			@io.swagger.v3.oas.annotations.Parameter(name = "page", example = "1"),
			@io.swagger.v3.oas.annotations.Parameter(name = "sortBy", example = "publishedAt")
		}
	)
	@ApiResponse(
		responseCode = "200",
		description = "Successfully fetched articles",
		content = @Content(schema = @Schema(implementation = NewsApiResponse.class))
	)
	@ApiResponse(responseCode = "400", description = "Exceeded maximum of 100 results")
	public ResponseEntity<NewsApiResponse> fetchArticles(
			@RequestParam(defaultValue = "new") @NotBlank String query,
			@RequestParam(defaultValue = "10") @Min(1) @Max(100) int pageSize,
			@RequestParam(defaultValue = "1") @Min(1) int page,
			@RequestParam(defaultValue = "publishedAt") String sortBy) {

		if (page * pageSize > 100) {
			throw new BusinessException(400, "Your account is limited to a maximum of 100 results");
		}

		NewsApiResponse newsApiResponse = articleService.fetchFromNewsApi(query, pageSize, page, sortBy);
		return ResponseEntity.ok(newsApiResponse);
	}

	// --------------------------------------------------
	// CREATE
	// --------------------------------------------------
	@PostMapping
	@Operation(
		summary = "Create new article",
		description = "Add a new Article to the system.",
		requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
			content = @Content(
				schema = @Schema(implementation = ArticleRequest.class),
				examples = @ExampleObject(value = """
					{
					  "title": "AI is transforming education",
					  "description": "How AI changes learning",
					  "imageUrl": "https://example.com/img.jpg",
					  "topicId": 3,
					  "content": "Full article content..."
					}
				""")
			)
		)
	)
	@ApiResponse(responseCode = "201", description = "Article successfully created")
	public ResponseEntity<Article> createArticle(@Valid @RequestBody ArticleRequest articleRequest) {
		return ResponseEntity.status(201).body(articleService.create(articleRequest));
	}

	// --------------------------------------------------
	// UPDATE
	// --------------------------------------------------
	@PutMapping("/{id}")
	@Operation(
		summary = "Update article",
		description = "Update an Article by its ID."
	)
	@ApiResponse(responseCode = "200", description = "Successfully updated article")
	@ApiResponse(responseCode = "404", description = "Article not found")
	public ResponseEntity<Article> updateArticle(
			@PathVariable Long id,
			@Valid @RequestBody ArticleRequest articleRequest) {

		return ResponseEntity.ok(articleService.update(id, articleRequest));
	}

	// --------------------------------------------------
	// TOGGLE ENABLED
	// --------------------------------------------------
	@PatchMapping("/{id}/enabled")
	@Operation(
		summary = "Toggle article enabled status",
		description = "Enable or disable an Article."
	)
	public ResponseEntity<Void> toggleEnabled(@PathVariable Long id) {
		articleService.toggleEnabled(id);
		return ResponseEntity.ok().build();
	}

	// --------------------------------------------------
	// DELETE
	// --------------------------------------------------
	@DeleteMapping("/{id}")
	@Operation(summary = "Delete article", description = "Delete an Article by its ID.")
	@ApiResponse(responseCode = "204", description = "Successfully deleted")
	public ResponseEntity<Void> deleteArticle(@PathVariable Long id) {
		articleService.deleteById(id);
		return ResponseEntity.noContent().build();
	}

	// --------------------------------------------------
	// WORD LINKING
	// --------------------------------------------------
	@PostMapping("/{articleId}/word/{wordId}")
	@Operation(summary = "Add a word to an article")
	public ResponseEntity<Article> addWordToArticle(@PathVariable Long articleId, @PathVariable Long wordId) {
		return ResponseEntity.ok(articleService.addWordToArticle(articleId, wordId));
	}

	@DeleteMapping("/{articleId}/word/{wordId}")
	@Operation(summary = "Remove a word from an article")
	public ResponseEntity<Article> removeWordFromArticle(@PathVariable Long articleId, @PathVariable Long wordId) {
		return ResponseEntity.ok(articleService.removeWordFromArticle(articleId, wordId));
	}

	@DeleteMapping("/{articleId}/word")
	@Operation(summary = "Remove all words from an article")
	public ResponseEntity<Article> removeAllWordsFromArticle(@PathVariable Long articleId) {
		return ResponseEntity.ok(articleService.removeAllWordsFromArticle(articleId));
	}
}
