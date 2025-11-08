package com.todaii.english.client.security;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.todaii.english.client.auth.RefreshTokenRepository;
import com.todaii.english.core.entity.User;
import com.todaii.english.core.entity.UserRefreshToken;
import com.todaii.english.infra.security.jwt.JwtUtility;
import com.todaii.english.shared.constants.SecurityConstants;
import com.todaii.english.shared.enums.error_code.AuthErrorCode;
import com.todaii.english.shared.exceptions.BusinessException;
import com.todaii.english.shared.request.RefreshTokenRequest;
import com.todaii.english.shared.response.AuthResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserTokenService {
    private final RefreshTokenRepository userRefreshTokenRepository;
    private final JwtUtility jwtUtility;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse generateToken(User user) {
        String accessToken = this.jwtUtility.generateAccessToken(user); // user implement JwtPrincipal
        String randomUUID = UUID.randomUUID().toString();

        UserRefreshToken userRefreshToken = new UserRefreshToken();
        userRefreshToken.setTokenHash(this.passwordEncoder.encode(randomUUID));
        userRefreshToken.setUser(user);

        // set thời gian hết hạn là 7 ngày
        userRefreshToken.setExpiresAt(
            LocalDateTime.now().plusMinutes(SecurityConstants.REFRESH_TOKEN_EXPIRATION_MINUTES)
        );

        this.userRefreshTokenRepository.save(userRefreshToken);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setAccessToken(accessToken);
        authResponse.setRefreshToken(randomUUID);

        return authResponse;
    }

    public AuthResponse refreshTokens(RefreshTokenRequest refreshTokenRequest) {
        String rawRefreshToken = refreshTokenRequest.getRefreshToken();

        List<UserRefreshToken> userRefreshTokens =
            this.userRefreshTokenRepository.findByUserEmail(refreshTokenRequest.getEmail());

        for (UserRefreshToken userRefreshToken : userRefreshTokens) {
            if (this.passwordEncoder.matches(rawRefreshToken, userRefreshToken.getTokenHash())) {
                LocalDateTime currentDate = LocalDateTime.now();
                if (userRefreshToken.getExpiresAt().isBefore(currentDate)) {
                    throw new BusinessException(AuthErrorCode.TOKEN_EXPIRED);
                }

                this.userRefreshTokenRepository.delete(userRefreshToken);

                return this.generateToken(userRefreshToken.getUser());
            }
        }

        throw new BusinessException(AuthErrorCode.TOKEN_NOT_FOUND);
    }

    public void revokeRefreshToken(RefreshTokenRequest refreshTokenRequest) {
        List<UserRefreshToken> userRefreshTokens =
            this.userRefreshTokenRepository.findByUserEmail(refreshTokenRequest.getEmail());

        boolean found = false;
        for (UserRefreshToken userRefreshToken : userRefreshTokens) {
            if (this.passwordEncoder.matches(refreshTokenRequest.getRefreshToken(), userRefreshToken.getTokenHash())) {
                this.userRefreshTokenRepository.delete(userRefreshToken);
                found = true;
                break;
            }
        }

        if (!found) {
            throw new BusinessException(AuthErrorCode.TOKEN_NOT_FOUND);
        }
    }
}
