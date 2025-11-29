package com.todaii.english.client.auth;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.todaii.english.client.security.CustomUserDetails;
import com.todaii.english.client.security.UserTokenService;
import com.todaii.english.client.user.UserService;
import com.todaii.english.shared.dto.UserDTO;
import com.todaii.english.shared.request.AuthRequest;
import com.todaii.english.shared.request.RefreshTokenRequest;
import com.todaii.english.shared.request.VerifyOtpRequest;
import com.todaii.english.shared.request.client.RegisterRequest;
import com.todaii.english.shared.request.client.ResetPasswordRequest;
import com.todaii.english.shared.response.AuthResponse;
import com.todaii.english.shared.utils.CookieUtils;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "APIs for user authentication, registration, and password management")
public class AuthApiController {

	private static final String USER_TYPE = "user";

	private final AuthenticationManager authenticationManager;
	private final UserService userService;
	private final UserTokenService userTokenService;

	@PostMapping("/register")
	@Operation(summary = "Register a new user", description = "Creates a new user account")
	public ResponseEntity<UserDTO> register(@Valid @RequestBody RegisterRequest registerRequest) {
		UserDTO userDTO = this.userService.createUser(registerRequest);
		return ResponseEntity.status(201).body(userDTO);
	}

	@PostMapping("/login")
	@Operation(summary = "User login", description = "Authenticates user and returns access & refresh tokens")
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

		return ResponseEntity.status(HttpStatus.OK)
				.header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
				.header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
				.body(authResponse);
	}

	@PostMapping("/new-token")
	@Operation(summary = "Refresh tokens", description = "Generates new access & refresh tokens using the refresh token")
	public ResponseEntity<AuthResponse> refreshToken(@RequestBody @Valid RefreshTokenRequest refreshTokenRequest) {
		AuthResponse authResponse = this.userTokenService.refreshTokens(refreshTokenRequest);
		return ResponseEntity.status(201).body(authResponse);
	}

	@PostMapping("/logout")
	@Operation(summary = "Logout user", description = "Revokes refresh token and removes authentication cookies")
	public ResponseEntity<Void> logout(@RequestParam @Email(message = "Email format is invalid") String email,
			@CookieValue(name = "user_refresh_token", required = false) String refreshToken) {
		this.userTokenService.revokeRefreshToken(email, refreshToken);

		ResponseCookie removedAccessToken = CookieUtils.removeCookie("user_access_token");
		ResponseCookie removedRefreshToken = CookieUtils.removeCookie("user_refresh_token");

		return ResponseEntity.ok()
				.header(HttpHeaders.SET_COOKIE, removedAccessToken.toString())
				.header(HttpHeaders.SET_COOKIE, removedRefreshToken.toString())
				.build();
	}

	@PostMapping("/verify-otp")
	@Operation(summary = "Verify OTP", description = "Verifies the OTP sent to the user's email")
	public ResponseEntity<Void> verifyOtp(@Valid @RequestBody VerifyOtpRequest verifyOtpRequest) {
		this.userService.verifyOtp(verifyOtpRequest);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/resend-otp")
	@Operation(summary = "Resend OTP", description = "Resends OTP to the user's email")
	public ResponseEntity<Void> resendOtp(@RequestParam @Email(message = "Email format is invalid") String email) {
		this.userService.resendOtp(email);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/forgot-password")
	@Operation(summary = "Request password reset", description = "Sends password reset instructions to user's email")
	public ResponseEntity<Void> forgotPassword(@RequestParam @Email(message = "Email format is invalid") String email) {
		this.userService.forgotPassword(email);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/reset-password")
	@Operation(summary = "Reset password", description = "Resets user's password using the provided reset token")
	public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
		this.userService.resetPassword(resetPasswordRequest);
		return ResponseEntity.ok().build();
	}
}
