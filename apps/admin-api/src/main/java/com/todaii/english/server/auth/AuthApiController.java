package com.todaii.english.server.auth;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.todaii.english.server.admin.AdminService;
import com.todaii.english.server.security.AdminTokenService;
import com.todaii.english.server.security.CustomAdminDetails;
import com.todaii.english.shared.request.AuthRequest;
import com.todaii.english.shared.request.RefreshTokenRequest;
import com.todaii.english.shared.response.AuthResponse;
import com.todaii.english.shared.utils.CookieUtils;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/auth")
public class AuthApiController {
	private static final String USER_TYPE = "admin";

	private final AuthenticationManager authenticationManager;
	private final AdminTokenService adminTokenService;
	private final AdminService adminService;

	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(@RequestBody @Valid AuthRequest authRequest) {
		String email = authRequest.getEmail();
		String password = authRequest.getPassword();

		Authentication authentication = new UsernamePasswordAuthenticationToken(email, password);
		Authentication result = authenticationManager.authenticate(authentication);

		CustomAdminDetails customAdminDetails = (CustomAdminDetails) result.getPrincipal();
		AuthResponse authResponse = this.adminTokenService.generateToken(customAdminDetails.getAdmin());

		this.adminService.updateLastLogin(email);

		// Create cookies
		ResponseCookie accessTokenCookie = CookieUtils.createAccessTokenCookie(authResponse.getAccessToken(),
				USER_TYPE);
		ResponseCookie refreshTokenCookie = CookieUtils.createRefreshTokenCookie(authResponse.getRefreshToken(),
				USER_TYPE);

		return ResponseEntity.status(HttpStatus.OK).header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
				.header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString()).body(authResponse);
	}

	@PostMapping("/new-token")
	public ResponseEntity<AuthResponse> refreshToken(@RequestBody @Valid RefreshTokenRequest refreshTokenRequest) {
		AuthResponse authResponse = this.adminTokenService.refreshTokens(refreshTokenRequest);
		return ResponseEntity.ok(authResponse);
	}

	@PostMapping("/logout")
	public ResponseEntity<Void> logout(@RequestParam @Email String email,
			@CookieValue(name = "admin_refresh_token", required = false) String refreshToken) {
		this.adminTokenService.revokeRefreshToken(email, refreshToken);

		ResponseCookie removedAccessToken = CookieUtils.removeCookie("admin_access_token");
		ResponseCookie removedRefreshToken = CookieUtils.removeCookie("admin_refresh_token");

		return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, removedAccessToken.toString())
				.header(HttpHeaders.SET_COOKIE, removedRefreshToken.toString()).build();
	}

}
