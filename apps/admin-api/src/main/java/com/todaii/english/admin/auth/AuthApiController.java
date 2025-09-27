package com.todaii.english.admin.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.todaii.english.infra.security.admin.CustomAdminDetails;
import com.todaii.english.infra.security.token.TokenService;
import com.todaii.english.shared.request.AuthRequest;
import com.todaii.english.shared.request.RefreshTokenRequest;
import com.todaii.english.shared.response.admin.AuthResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthApiController {
	private final AuthenticationManager authenticationManager;
	private final TokenService tokenService;

	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(@RequestBody @Valid AuthRequest authRequest) {
		String email = authRequest.getEmail();
		String password = authRequest.getPassword();

		Authentication authentication = new UsernamePasswordAuthenticationToken(email, password);
		Authentication result = authenticationManager.authenticate(authentication);

		CustomAdminDetails customAdminDetails = (CustomAdminDetails) result.getPrincipal();
		AuthResponse authResponse = this.tokenService.generateToken(customAdminDetails.getAdmin());

		return ResponseEntity.status(HttpStatus.OK).body(authResponse);
	}

	@PostMapping("/token/refresh")
	public ResponseEntity<?> refreshToken(@RequestBody @Valid RefreshTokenRequest refreshTokenRequest) {
		AuthResponse authResponse = this.tokenService.refreshTokens(refreshTokenRequest);
		return ResponseEntity.ok(authResponse);
	}

	@PostMapping("/logout")
	public ResponseEntity<?> logout(@RequestBody RefreshTokenRequest refreshTokenRequest) {
		this.tokenService.revokeRefreshToken(refreshTokenRequest);
		return ResponseEntity.ok().build();
	}
//
//	@GetMapping("/verify-email")
//	public ResponseEntity<?> verifyEmail(@RequestParam("token") String token) {
//		boolean success = this.tokenService.verifyEmailToken(token);
//
//		if (!success) {
//			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//					.body(Map.of("message", "Invalid or expired verification token"));
//		}
//		return ResponseEntity.ok(Map.of("message", "Email verified successfully"));
//	}
//
//	@PostMapping("/forgot-password")
//	public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> payload) {
//		String email = payload.get("email");
//		this.tokenService.sendResetPasswordEmail(email);
//
//		return ResponseEntity.ok(Map.of("message", "Password reset link has been sent to your email"));
//	}
//
//	@PostMapping("/reset-password")
//	public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
//		boolean success = this.tokenService.resetPassword(request.getToken(), request.getNewPassword());
//
//		if (!success) {
//			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//					.body(Map.of("message", "Invalid or expired reset token"));
//		}
//		return ResponseEntity.ok(Map.of("message", "Password reset successfully"));
//	}

}
