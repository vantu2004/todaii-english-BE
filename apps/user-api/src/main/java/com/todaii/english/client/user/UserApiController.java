package com.todaii.english.client.user;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.todaii.english.client.security.CustomUserDetails;
import com.todaii.english.shared.dto.UserDTO;
import com.todaii.english.shared.request.UpdateProfileRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
@Tag(name = "User", description = "APIs for user profile and saved content")
public class UserApiController {
	private final UserService userService;

	@GetMapping("/me")
	@Operation(summary = "Get current user profile", description = "Retrieve the profile of the currently authenticated user")
	public ResponseEntity<UserDTO> getProfile(Authentication authentication) {
		CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
		Long currentUserId = principal.getUser().getId();
		return ResponseEntity.ok(userService.getUserById(currentUserId));
	}

	@PutMapping("/me")
	@Operation(summary = "Update user profile", description = "Update the profile information of the currently authenticated user")
	public ResponseEntity<UserDTO> updateProfile(Authentication authentication,
			@Valid @RequestBody UpdateProfileRequest updateProfileRequest) {
		CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
		Long currentUserId = principal.getUser().getId();
		return ResponseEntity.ok(userService.updateProfile(currentUserId, updateProfileRequest));
	}

	@PutMapping("/article/{articleId}")
	@Operation(summary = "Toggle saved article", description = "Save or unsave an article for the currently authenticated user")
	public ResponseEntity<Void> toggleSavedArticle(Authentication authentication, @PathVariable Long articleId) {
		CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
		Long currentUserId = principal.getUser().getId();
		userService.toggleSavedArticle(currentUserId, articleId);
		return ResponseEntity.ok().build();
	}

	@PutMapping("/video/{videoId}")
	@Operation(summary = "Toggle saved video", description = "Save or unsave a video for the currently authenticated user")
	public ResponseEntity<Void> toggleSavedVideo(Authentication authentication, @PathVariable Long videoId) {
		CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
		Long currentUserId = principal.getUser().getId();
		userService.toggleSavedVideo(currentUserId, videoId);
		return ResponseEntity.ok().build();
	}
}
