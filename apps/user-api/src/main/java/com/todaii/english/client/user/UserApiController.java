package com.todaii.english.client.user;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.todaii.english.client.security.CustomUserDetails;
import com.todaii.english.shared.dto.UserDTO;
import com.todaii.english.shared.request.UpdateProfileRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserApiController {
	private final UserService userService;

	// Spring Security sẽ tự động inject authentication khi xử lý request
	@GetMapping("/me")
	public ResponseEntity<UserDTO> getProfile(Authentication authentication) {
		CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
		Long currentUserId = principal.getUser().getId();

		return ResponseEntity.ok(userService.getUserById(currentUserId));
	}

	@PutMapping("/me")
	public ResponseEntity<UserDTO> updateProfile(Authentication authentication,
			@Valid @RequestBody UpdateProfileRequest updateProfileRequest) {
		CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
		Long currentUserId = principal.getUser().getId();

		return ResponseEntity.ok(userService.updateProfile(currentUserId, updateProfileRequest));
	}

	// xử lý lưu/bỏ lưu article
	@PutMapping("/article/{articleId}")
	public ResponseEntity<Void> toggleSavedArticle(Authentication authentication, @PathVariable Long articleId) {
		CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
		Long currentUserId = principal.getUser().getId();

		userService.toggleSavedArticle(currentUserId, articleId);

		return ResponseEntity.ok().build();
	}

	// xử lý lưu/bỏ lưu video
	@PutMapping("/video/{videoId}")
	public ResponseEntity<Void> toggleSavedVideo(Authentication authentication, @PathVariable Long videoId) {
		CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
		Long currentUserId = principal.getUser().getId();

		userService.toggleSavedVideo(currentUserId, videoId);

		return ResponseEntity.ok().build();
	}
}
