package com.todaii.english.infra.security.jwt;

import java.util.Date;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Component;
import com.todaii.english.core.security.JwtPrincipal;
import com.todaii.english.shared.constants.SecurityConstants;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtUtility {
	private final JwtProperties jwtProperties;

	public String generateAccessToken(JwtPrincipal principal) {
		String subject = principal.getId() + "," + principal.getDisplayName();

		Date now = new Date();
		Date expiry = new Date(now.getTime() + SecurityConstants.ACCESS_TOKEN_EXPIRATION_MINUTES * 60 * 1000L);

		return Jwts.builder().issuer(jwtProperties.getIssuer()).subject(subject)
				.claim("roles", String.join(",", principal.getRoleCodes())) // User thì rỗng
				.claim("actorType", principal.getActorType()).issuedAt(now).expiration(expiry)
				.signWith(Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes()), Jwts.SIG.HS512).compact();
	}

	public Claims validateAccessToken(String token) throws JwtValidationException {
		try {
			SecretKeySpec secretKeySpec = new SecretKeySpec(jwtProperties.getSecret().getBytes(),
					SecurityConstants.SECRET_KEY_ALGORITHM);

			return Jwts.parser().verifyWith(secretKeySpec).build().parseSignedClaims(token).getPayload();

		} catch (ExpiredJwtException e) {
			throw new JwtValidationException("Access token expired!", e);
		} catch (Exception e) {
			throw new JwtValidationException("Access token invalid!", e);
		}
	}
}
