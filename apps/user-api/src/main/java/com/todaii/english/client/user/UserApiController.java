package com.todaii.english.client.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.todaii.english.client.security.CustomUserDetails;
import com.todaii.english.core.entity.Article;
import com.todaii.english.shared.request.UpdateProfileRequest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserApiController {
	private final UserService userService;
	private final UserArticleService userArticleService;


	// Spring Security sẽ tự động inject authentication khi xử lý request
	@GetMapping("/me")
	public ResponseEntity<?> getProfile(Authentication authentication) {
		CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
		Long currentUserId = principal.getUser().getId();

		return ResponseEntity.ok(this.userService.getUserById(currentUserId));
	}

	@PutMapping("/me")
	public ResponseEntity<?> updateProfile(Authentication authentication,
			@Valid @RequestBody UpdateProfileRequest updateProfileRequest) {
		CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
		Long currentUserId = principal.getUser().getId();

		return ResponseEntity.ok(this.userService.updateProfile(currentUserId, updateProfileRequest));
	}
	
	/**
	 * Get saved articles for current user with pagination
	 * GET /api/v1/user/me/saved-articles?page=0&size=10&sort=createdAt,desc
	 */
	@GetMapping("/me/saved-articles")
	public ResponseEntity<Page<Article>> getMySavedArticles(
			Authentication authentication,
			@RequestParam(defaultValue = "1")  @Min(value = 1, message = "Page must be at least 1") int page,
			@RequestParam(defaultValue = "10") @Min(value = 1, message = "Size must be at least 1") int size,
			@RequestParam(defaultValue = "id") String sortBy, @RequestParam(defaultValue = "desc") String direction) {
		
		CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
		Long currentUserId = principal.getUser().getId();
		
		// Parse sort parameters
		Sort.Direction sortDirection = Sort.Direction.fromString(direction);
		Sort sortBys = Sort.by(sortDirection, sortBy);
		Pageable pageable = PageRequest.of(page - 1, size, sortBys);
		
		Page<Article> articles = userArticleService.getSavedArticlesByUserPageable(currentUserId, pageable);
		return ResponseEntity.ok(articles);
	}
	
	/**
	 * Save an article for current user
	 * POST /api/v1/user/me/saved-articles/{articleId}
	 */
	@PostMapping("/me/saved-articles/{articleId}")
	public ResponseEntity<?> saveArticle(
			Authentication authentication,
			@PathVariable Long articleId) {
		
		CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
		Long currentUserId = principal.getUser().getId();
		
		userArticleService.saveArticleForUser(currentUserId, articleId);
		return ResponseEntity.ok().body("Article saved successfully");
	}
	
	/**
	 * Remove a saved article for current user
	 * DELETE /api/v1/user/me/saved-articles/{articleId}
	 */
	@DeleteMapping("/me/saved-articles/{articleId}")
	public ResponseEntity<?> removeSavedArticle(
			Authentication authentication,
			@PathVariable Long articleId) {
		
		CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
		Long currentUserId = principal.getUser().getId();
		
		userArticleService.removeSavedArticleFromUser(currentUserId, articleId);
		return ResponseEntity.ok().body("Article removed from saved list");
	}
	
	@GetMapping("/me/saved-articles/{articleId}/check")
	public ResponseEntity<?> checkArticleSaved(
			Authentication authentication,
			@PathVariable Long articleId) {
		
		CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
		Long currentUserId = principal.getUser().getId();
		
		boolean isSaved = userArticleService.isArticleSavedByUser(currentUserId, articleId);
		return ResponseEntity.ok().body(isSaved);
	}
	
	/**
	 * Get count of saved articles for current user
	 * GET /api/v1/user/me/saved-articles/count
	 */
	@GetMapping("/me/saved-articles/count")
	public ResponseEntity<?> countMySavedArticles(Authentication authentication) {
		CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
		Long currentUserId = principal.getUser().getId();
		
		long count = userArticleService.countSavedArticlesByUser(currentUserId);
		return ResponseEntity.ok().body(count);
	}
	
	/**
	 * Clear all saved articles for current user
	 * DELETE /api/v1/user/me/saved-articles
	 */
	@DeleteMapping("/me/saved-articles")
	public ResponseEntity<?> clearAllSavedArticles(Authentication authentication) {
		CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
		Long currentUserId = principal.getUser().getId();
		
		userArticleService.clearAllSavedArticlesForUser(currentUserId);
		return ResponseEntity.ok().body("All saved articles cleared");
	}
	
	
	
}
