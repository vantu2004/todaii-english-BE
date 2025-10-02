package com.todaii.english.client.user;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.todaii.english.client.security.CustomUserDetails;
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
}
