package com.todaii.english.infra.security.server;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.todaii.english.core.entity.Admin;
import com.todaii.english.core.entity.AdminRefreshToken;
import com.todaii.english.core.refresh_token.AdminRefreshTokenRepository;
import com.todaii.english.infra.security.jwt.JwtUtility;
import com.todaii.english.shared.constants.SecurityConstants;
import com.todaii.english.shared.enums.error_code.AuthErrorCode;
import com.todaii.english.shared.exceptions.BusinessException;
import com.todaii.english.shared.request.RefreshTokenRequest;
import com.todaii.english.shared.response.AuthResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminTokenService {
	private final AdminRefreshTokenRepository adminRefreshTokenRepository;
	private final JwtUtility jwtUtility;
	private final PasswordEncoder passwordEncoder;

	public AuthResponse generateToken(Admin admin) {
		String accessToken = this.jwtUtility.generateAccessToken(admin); // admin implement JwtPrincipal
		String randomUUID = UUID.randomUUID().toString();

		AdminRefreshToken adminRefreshToken = new AdminRefreshToken();
		adminRefreshToken.setTokenHash(this.passwordEncoder.encode(randomUUID));
		adminRefreshToken.setAdmin(admin);

		// set thời gian hết hạn là 7 ngày
		adminRefreshToken
				.setExpiresAt(LocalDateTime.now().plusMinutes(SecurityConstants.REFRESH_TOKEN_EXPIRATION_MINUTES));

		this.adminRefreshTokenRepository.save(adminRefreshToken);

		AuthResponse authResponse = new AuthResponse();
		authResponse.setAccessToken(accessToken);
		authResponse.setRefreshToken(randomUUID);

		return authResponse;
	}

	public AuthResponse refreshTokens(RefreshTokenRequest refreshTokenRequest) {
		String rawRefreshToken = refreshTokenRequest.getRefreshToken();

		List<AdminRefreshToken> adminRefreshTokens = this.adminRefreshTokenRepository
				.findByAdminEmail(refreshTokenRequest.getEmail());

		for (AdminRefreshToken adminRefreshToken : adminRefreshTokens) {
			if (this.passwordEncoder.matches(rawRefreshToken, adminRefreshToken.getTokenHash())) {
				LocalDateTime currentDate = LocalDateTime.now();
				if (adminRefreshToken.getExpiresAt().isBefore(currentDate)) {
					throw new BusinessException(AuthErrorCode.TOKEN_EXPIRED);
				}

				this.adminRefreshTokenRepository.delete(adminRefreshToken);

				return this.generateToken(adminRefreshToken.getAdmin());
			}
		}

		throw new BusinessException(AuthErrorCode.TOKEN_NOT_FOUND);
	}

	public void revokeRefreshToken(RefreshTokenRequest refreshTokenRequest) {
		List<AdminRefreshToken> adminRefreshTokens = this.adminRefreshTokenRepository
				.findByAdminEmail(refreshTokenRequest.getEmail());

		boolean found = false;
		for (AdminRefreshToken adminRefreshToken : adminRefreshTokens) {
			if (this.passwordEncoder.matches(refreshTokenRequest.getRefreshToken(), adminRefreshToken.getTokenHash())) {
				this.adminRefreshTokenRepository.delete(adminRefreshToken);
				found = true;
				break;
			}
		}

		if (!found) {
			throw new BusinessException(AuthErrorCode.TOKEN_NOT_FOUND);
		}
	}

}
