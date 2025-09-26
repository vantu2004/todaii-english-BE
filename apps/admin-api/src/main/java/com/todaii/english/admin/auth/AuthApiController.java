package com.todaii.english.admin.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
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
	private static final Logger LOGGER = LoggerFactory.getLogger(AuthApiController.class);

	private final AuthenticationManager authenticationManager;
	private final TokenService tokenService;

	@PostMapping("/token")
	public ResponseEntity<AuthResponse> getAccessToken(@RequestBody @Valid AuthRequest authRequest) {
		try {
			String email = authRequest.getEmail();
			String password = authRequest.getPassword();

			Authentication authentication = new UsernamePasswordAuthenticationToken(email, password);
			Authentication result = authenticationManager.authenticate(authentication);

			CustomAdminDetails customAdminDetails = (CustomAdminDetails) result.getPrincipal();
			AuthResponse authResponse = this.tokenService.generateToken(customAdminDetails.getAdmin());

			return ResponseEntity.status(HttpStatus.OK).body(authResponse);
		} catch (AuthenticationException e) {
			LOGGER.error(e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

	@PostMapping("/token/refresh")
	public ResponseEntity<?> refreshToken(@RequestBody @Valid RefreshTokenRequest refreshTokenRequest) {
		AuthResponse authResponse = this.tokenService.refreshTokens(refreshTokenRequest);
		return ResponseEntity.ok(authResponse);
	}
}
