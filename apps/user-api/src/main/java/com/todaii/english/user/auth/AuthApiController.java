package com.todaii.english.user.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.todaii.english.core.admin.admin.AdminService;
import com.todaii.english.core.user.user.User;
import com.todaii.english.core.user.user.UserService;
import com.todaii.english.infra.security.admin.AdminTokenService;
import com.todaii.english.infra.security.admin.CustomAdminDetails;
import com.todaii.english.infra.security.user.UserTokenService;
import com.todaii.english.shared.request.AuthRequest;
import com.todaii.english.shared.request.user.RegisterRequest;
import com.todaii.english.shared.response.AuthResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthApiController {
	private final AuthenticationManager authenticationManager;
	private final UserService userService;
	private final UserTokenService userTokenService;

	@PostMapping("/register")
	public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
		User user = this.userService.createUser(registerRequest);
		return ResponseEntity.ok(user);
	}

//	@PostMapping("/login")
//	public ResponseEntity<AuthResponse> login(@RequestBody @Valid AuthRequest authRequest) {
//		String email = authRequest.getEmail();
//		String password = authRequest.getPassword();
//
//		Authentication authentication = new UsernamePasswordAuthenticationToken(email, password);
//		Authentication result = authenticationManager.authenticate(authentication);
//
//		CustomAdminDetails customAdminDetails = (CustomAdminDetails) result.getPrincipal();
//		AuthResponse authResponse = this.userTokenService.generateToken(customAdminDetails.getAdmin());
//
//		this.userService.updateLastLogin(email);
//
//		return ResponseEntity.status(HttpStatus.OK).body(authResponse);
//	}

//	/** POST /google */
//	@PostMapping("/google")
//	public ResponseEntity<?> loginGoogle(@RequestBody Map<String, Object> payload) {
//		LOGGER.info("Google login with payload {}", payload);
//		return ResponseEntity.ok(Map.of("token", "google-jwt-token"));
//	}
//
//	/** POST /logout */
//	@PostMapping("/logout")
//	public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
//		LOGGER.info("Logout with token: {}", token);
//		return ResponseEntity.ok(Map.of("message", "Logged out"));
//	}
//
//	/** POST /verify-email */
//	@PostMapping("/verify-email")
//	public ResponseEntity<?> verifyEmail(@RequestBody Map<String, String> payload) {
//		String code = payload.get("code");
//		LOGGER.info("Verify email with code {}", code);
//		return ResponseEntity.ok(Map.of("message", "Email verified"));
//	}
//
//	/** POST /forgot-password */
//	@PostMapping("/forgot-password")
//	public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> payload) {
//		String email = payload.get("email");
//		LOGGER.info("Forgot password request for {}", email);
//		return ResponseEntity.ok(Map.of("message", "Reset link sent"));
//	}
//
//	/** POST /reset-password/{token} */
//	@PostMapping("/reset-password/{token}")
//	public ResponseEntity<?> resetPassword(@PathVariable String token, @RequestBody Map<String, String> payload) {
//		String newPassword = payload.get("password");
//		LOGGER.info("Reset password with token {} and new password {}", token, newPassword);
//		return ResponseEntity.ok(Map.of("message", "Password reset success"));
//	}
}
