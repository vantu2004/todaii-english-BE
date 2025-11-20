package com.todaii.english.client.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.todaii.english.client.security.CustomUserDetails;
import com.todaii.english.core.entity.Article;
import com.todaii.english.shared.dto.ArticleDTO;
import com.todaii.english.shared.request.UpdateProfileRequest;
import com.todaii.english.shared.response.PagedResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserApiController {
	private final UserService userService;


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
	
	@GetMapping("/saved-articles")
	public ResponseEntity<PagedResponse<ArticleDTO>> getSavedArticles(Authentication authentication,
	        @RequestParam(defaultValue = "1") int page,
	        @RequestParam(defaultValue = "10") int size) {
		
		
		CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
		Long currentUserId = principal.getUser().getId();

	    Page<ArticleDTO> articles = userService.getSavedArticles(currentUserId, page, size);
	    
	    PagedResponse<ArticleDTO> response = new PagedResponse<>(
	            articles.getContent(),
	            page,
	            size,
	            articles.getTotalElements(),
	            articles.getTotalPages(),
	            articles.isFirst(),
	            articles.isLast(),
	            "updatedAt",
	            "desc"
	        );

	    return ResponseEntity.ok(response);
	}
	
	@GetMapping("/is-article-saved/{articleId}")
	public ResponseEntity<Boolean> isArticleSaved(
	        Authentication authentication,
	        @PathVariable Long articleId) {

	    CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
	    Long currentUserId = principal.getUser().getId();

	    boolean saved = userService.isArticleSaved(currentUserId, articleId);
	    return ResponseEntity.ok(saved);
	}


	
	
}
