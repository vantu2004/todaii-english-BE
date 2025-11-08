package com.todaii.english.client.article;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.todaii.english.core.entity.Article;
import com.todaii.english.shared.response.PagedResponse;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/article")
public class ArticleApiController {
	private final ArticleService articleService;

	// lấy 10 articles gần đây nhất
	@GetMapping("/latest")
	public ResponseEntity<List<Article>> getLatestArticles() {
		return ResponseEntity.ok(articleService.getLatestArticles());
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

}
