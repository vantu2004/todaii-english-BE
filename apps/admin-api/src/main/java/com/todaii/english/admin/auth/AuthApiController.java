package com.todaii.english.admin.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.todaii.english.core.admin.admin.AdminService;
import com.todaii.english.infra.security.admin.CustomAdminDetails;
import com.todaii.english.infra.security.token.TokenService;
import com.todaii.english.shared.request.AuthRequest;
import com.todaii.english.shared.request.RefreshTokenRequest;
import com.todaii.english.shared.request.VerifyOtpRequest;
import com.todaii.english.shared.response.admin.AuthResponse;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/auth")
public class AuthApiController {
	private final AuthenticationManager authenticationManager;
	private final TokenService tokenService;
	private final AdminService adminService;

	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(@RequestBody @Valid AuthRequest authRequest) {
		String email = authRequest.getEmail();
		String password = authRequest.getPassword();

		Authentication authentication = new UsernamePasswordAuthenticationToken(email, password);
		Authentication result = authenticationManager.authenticate(authentication);

		CustomAdminDetails customAdminDetails = (CustomAdminDetails) result.getPrincipal();
		AuthResponse authResponse = this.tokenService.generateToken(customAdminDetails.getAdmin());

		this.adminService.updateLastLogin(email);

		return ResponseEntity.status(HttpStatus.OK).body(authResponse);
	}

	@PostMapping("/new-token")
	public ResponseEntity<?> refreshToken(@RequestBody @Valid RefreshTokenRequest refreshTokenRequest) {
		AuthResponse authResponse = this.tokenService.refreshTokens(refreshTokenRequest);
		return ResponseEntity.ok(authResponse);
	}

	@PostMapping("/logout")
	public ResponseEntity<?> logout(@RequestBody RefreshTokenRequest refreshTokenRequest) {
		this.tokenService.revokeRefreshToken(refreshTokenRequest);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/verify-otp")
	public ResponseEntity<?> verifyOtp(@Valid @RequestBody VerifyOtpRequest verifyOtpRequest) {
		this.adminService.verifyOtp(verifyOtpRequest);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/resend-otp")
	public ResponseEntity<?> resendOtp(
			@Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "Email format is invalid") @RequestParam String email) {
		this.adminService.resendOtp(email);
		return ResponseEntity.ok().build();
	}
}
