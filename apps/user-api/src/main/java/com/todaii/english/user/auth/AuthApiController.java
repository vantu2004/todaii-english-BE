package com.todaii.english.user.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.todaii.english.core.user.User;
import com.todaii.english.core.user.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthApiController {
	private static final Logger LOGGER = LoggerFactory.getLogger(AuthApiController.class);

	private final UserService userService;

	/** GET /check-auth */
	@GetMapping("/check-auth")
	public ResponseEntity<?> checkAuth(@RequestHeader(value = "Authorization", required = false) String token) {
		// verifyToken logic
		if (token == null || token.isBlank()) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing token");
		}
		LOGGER.info("Check-auth with token: {}", token);
		return ResponseEntity.ok(Map.of("authenticated", true));
	}

	@PostMapping("/register")
	public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
		User user = this.userService.createUser(registerRequest.getEmail(), registerRequest.getPassword(),
				registerRequest.getDisplayName());

		return ResponseEntity.ok(user);
	}

	/** POST /login */
	@PostMapping("/login")
	public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
		LOGGER.info("Login request: {}", req.getEmail());
		return ResponseEntity.ok(Map.of("token", "jwt-token-sample"));
	}

	/** POST /google */
	@PostMapping("/google")
	public ResponseEntity<?> loginGoogle(@RequestBody Map<String, Object> payload) {
		LOGGER.info("Google login with payload {}", payload);
		return ResponseEntity.ok(Map.of("token", "google-jwt-token"));
	}

	/** POST /logout */
	@PostMapping("/logout")
	public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
		LOGGER.info("Logout with token: {}", token);
		return ResponseEntity.ok(Map.of("message", "Logged out"));
	}

	/** POST /verify-email */
	@PostMapping("/verify-email")
	public ResponseEntity<?> verifyEmail(@RequestBody Map<String, String> payload) {
		String code = payload.get("code");
		LOGGER.info("Verify email with code {}", code);
		return ResponseEntity.ok(Map.of("message", "Email verified"));
	}

	/** POST /forgot-password */
	@PostMapping("/forgot-password")
	public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> payload) {
		String email = payload.get("email");
		LOGGER.info("Forgot password request for {}", email);
		return ResponseEntity.ok(Map.of("message", "Reset link sent"));
	}

	/** POST /reset-password/{token} */
	@PostMapping("/reset-password/{token}")
	public ResponseEntity<?> resetPassword(@PathVariable String token, @RequestBody Map<String, String> payload) {
		String newPassword = payload.get("password");
		LOGGER.info("Reset password with token {} and new password {}", token, newPassword);
		return ResponseEntity.ok(Map.of("message", "Password reset success"));
	}
}
