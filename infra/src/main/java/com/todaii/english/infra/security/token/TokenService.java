package com.todaii.english.infra.security.token;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.todaii.english.core.admin.admin.Admin;
import com.todaii.english.core.refresh_token.RefreshToken;
import com.todaii.english.core.refresh_token.RefreshTokenRepository;
import com.todaii.english.infra.security.jwt.JwtUtility;
import com.todaii.english.shared.constants.SecurityConstants;
import com.todaii.english.shared.enums.error_code.AuthErrorCode;
import com.todaii.english.shared.exceptions.BusinessException;
import com.todaii.english.shared.request.RefreshTokenRequest;
import com.todaii.english.shared.response.admin.AuthResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenService {
	private final RefreshTokenRepository refreshTokenRepository;
	private final JwtUtility jwtUtility;
	private final PasswordEncoder passwordEncoder;

	public AuthResponse generateToken(Admin admin) {
		String accessToken = this.jwtUtility.generateAccessToken(admin);
		String randomUUID = UUID.randomUUID().toString();

		// lưu refresh token của admin vào db
		RefreshToken refreshToken = new RefreshToken();
		refreshToken.setTokenHash(this.passwordEncoder.encode(randomUUID));
		refreshToken.setAdmin(admin);

		// set thời gian hết hạn là 7 ngày
		refreshToken.setExpiresAt(LocalDateTime.now().plusMinutes(SecurityConstants.REFRESH_TOKEN_EXPIRATION_MINUTES));

		this.refreshTokenRepository.save(refreshToken);

		AuthResponse authResponse = new AuthResponse();
		authResponse.setAccessToken(accessToken);
		authResponse.setRefreshToken(randomUUID);

		return authResponse;
	}

	public AuthResponse refreshTokens(RefreshTokenRequest refreshTokenRequest) {
		String rawRefreshToken = refreshTokenRequest.getRefreshToken();

		List<RefreshToken> refreshTokens = this.refreshTokenRepository.findByAdminEmail(refreshTokenRequest.getEmail());

		for (RefreshToken refreshToken : refreshTokens) {
			if (this.passwordEncoder.matches(rawRefreshToken, refreshToken.getTokenHash())) {
				LocalDateTime currentDate = LocalDateTime.now();
				if (refreshToken.getExpiresAt().isBefore(currentDate)) {
					throw new BusinessException(AuthErrorCode.TOKEN_EXPIRED);
				}

				this.refreshTokenRepository.delete(refreshToken);

				return this.generateToken(refreshToken.getAdmin());
			}
		}

		throw new BusinessException(AuthErrorCode.TOKEN_NOT_FOUND);
	}
}
