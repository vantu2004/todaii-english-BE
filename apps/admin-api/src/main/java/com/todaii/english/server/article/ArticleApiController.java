package com.todaii.english.server.article;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.todaii.english.shared.exceptions.BusinessException;
import com.todaii.english.shared.response.NewsApiResponse;

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

	@GetMapping("/news-api")
	public ResponseEntity<NewsApiResponse> fetchArticles(@RequestParam(defaultValue = "bitcoin") @NotBlank String query,
			@RequestParam(defaultValue = "10") @Min(1) @Max(100) int pageSize,
			@RequestParam(defaultValue = "1") @Min(1) int page, @RequestParam(defaultValue = "publishedAt") String sortBy) {
		if (page * pageSize > 100) {
			throw new BusinessException(400, "Your account is limited to a max of 100 results");
		}

		NewsApiResponse newsApiResponse = articleService.fetchFromNewsApi(query, pageSize, page, sortBy);
		return ResponseEntity.ok(newsApiResponse);
	}
}
