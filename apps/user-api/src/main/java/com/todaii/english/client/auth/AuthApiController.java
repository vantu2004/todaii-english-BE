package com.todaii.english.client.auth;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.todaii.english.client.security.CustomUserDetails;
import com.todaii.english.client.security.UserTokenService;
import com.todaii.english.client.user.UserService;
import com.todaii.english.core.entity.User;
import com.todaii.english.shared.request.AuthRequest;
import com.todaii.english.shared.request.RefreshTokenRequest;
import com.todaii.english.shared.request.VerifyOtpRequest;
import com.todaii.english.shared.request.client.RegisterRequest;
import com.todaii.english.shared.request.client.ResetPasswordRequest;
import com.todaii.english.shared.response.AuthResponse;
import com.todaii.english.shared.utils.CookieUtils;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthApiController {
	private static final String USER_TYPE = "user";

	private final AuthenticationManager authenticationManager;
	private final UserService userService;
	private final UserTokenService userTokenService;

	@PostMapping("/register")
	public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
		User user = this.userService.createUser(registerRequest);
		return ResponseEntity.status(201).body(user);
	}

	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(@RequestBody @Valid AuthRequest authRequest) {
		String email = authRequest.getEmail();
		String password = authRequest.getPassword();

		Authentication authentication = new UsernamePasswordAuthenticationToken(email, password);
		Authentication result = authenticationManager.authenticate(authentication);

		CustomUserDetails customUserDetails = (CustomUserDetails) result.getPrincipal();
		AuthResponse authResponse = this.userTokenService.generateToken(customUserDetails.getUser());

		this.userService.updateLastLogin(email);

		// Create cookies
		ResponseCookie accessTokenCookie = CookieUtils.createAccessTokenCookie(authResponse.getAccessToken(),
				USER_TYPE);
		ResponseCookie refreshTokenCookie = CookieUtils.createRefreshTokenCookie(authResponse.getRefreshToken(),
				USER_TYPE);

		return ResponseEntity.status(HttpStatus.OK).header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
				.header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString()).body(authResponse);
	}

	@PostMapping("/new-token")
	public ResponseEntity<?> refreshToken(@RequestBody @Valid RefreshTokenRequest refreshTokenRequest) {
		AuthResponse authResponse = this.userTokenService.refreshTokens(refreshTokenRequest);
		return ResponseEntity.status(201).body(authResponse);
	}

	@PostMapping("/logout")
	public ResponseEntity<?> logout(@RequestBody RefreshTokenRequest refreshTokenRequest) {
		this.userTokenService.revokeRefreshToken(refreshTokenRequest);
		ResponseCookie removedAccessToken = CookieUtils.removeCookie("user_access_token");
		ResponseCookie removedRefreshToken = CookieUtils.removeCookie("user_refresh_token");

		return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, removedAccessToken.toString())
				.header(HttpHeaders.SET_COOKIE, removedRefreshToken.toString()).build();
	}

	@PostMapping("/verify-otp")
	public ResponseEntity<?> verifyOtp(@Valid @RequestBody VerifyOtpRequest verifyOtpRequest) {
		this.userService.verifyOtp(verifyOtpRequest);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/resend-otp")
	public ResponseEntity<?> resendOtp(
			@Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "Email format is invalid") @RequestParam String email) {
		this.userService.resendOtp(email);
		return ResponseEntity.ok().build();
	}

	@GetMapping("forgot-password")
	public ResponseEntity<?> forgotPassword(
			@Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "Email format is invalid") @RequestParam String email) {
		this.userService.forgotPassword(email);
		return ResponseEntity.ok().build();
	}

	@PostMapping("reset-password")
	public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
		this.userService.resetPassword(resetPasswordRequest);
		return ResponseEntity.ok().build();
	}
}
