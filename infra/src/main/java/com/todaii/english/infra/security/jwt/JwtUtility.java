package com.todaii.english.infra.security.jwt;

import java.util.Date;
import java.util.stream.Collectors;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Component;

import com.todaii.english.core.admin.admin.Admin;
import com.todaii.english.core.admin.admin.AdminRole;
import com.todaii.english.shared.constants.SecurityConstants;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Component
@RequiredArgsConstructor
@Getter
@Setter
public class JwtUtility {
	private final JwtProperties jwtProperties;

	public String generateAccessToken(Admin admin) {
		if (admin == null || admin.getId() == null || admin.getDisplayName() == null || admin.getRoles() == null) {
			throw new IllegalArgumentException("Admin object is null or its fields have null values");
		}

		// Subject = id + displayName
		String subject = String.format("%s,%s", admin.getId(), admin.getDisplayName());

		// vấn phải để date vì Jwt api yêu cầu date
		Date now = new Date();

		// Thời gian hết hạn (15 phút)
		long expirationInMs = SecurityConstants.ACCESS_TOKEN_EXPIRATION_MINUTES * 60 * 1000L;
		Date expiryDate = new Date(now.getTime() + expirationInMs);

		// Lấy role (giả sử 1 admin có thể có nhiều role → join thành chuỗi)
		String roles = admin.getRoles().stream().map(AdminRole::getCode).collect(Collectors.joining(","));

		return Jwts.builder().issuer(jwtProperties.getIssuer()).subject(subject).claim("roles", roles)
				.issuedAt(now).expiration(expiryDate)
				.signWith(Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes()), Jwts.SIG.HS512).compact();
	}

	public Claims validateAccessToken(String token) throws JwtValidationException {
		try {
			/*
			 * Tạo khóa bí mật (SecretKeySpec) từ chuỗi secretKey.
			 * 
			 * - secretKey.getBytes(): chuyển chuỗi thành mảng byte.
			 * 
			 * - SECRET_KEY_ALGORITHM: thuật toán mã hóa, thường là "HmacSHA256". Đây là
			 * thuật toán dùng để xác thực chữ ký của JWT.
			 * 
			 * SecretKeySpec là một implementation của javax.crypto.SecretKey.
			 */
			SecretKeySpec secretKeySpec = new SecretKeySpec(jwtProperties.getSecret().getBytes(),
					SecurityConstants.SECRET_KEY_ALGORITHM);

			// parser() trả về JwtParserBuilder để xử lý chuỗi JWT.
			return Jwts.parser()
					/*
					 * Cung cấp khóa bí mật để parser có thể kiểm tra chữ ký của token. Nếu chữ ký
					 * không khớp, token sẽ bị từ chối.
					 * 
					 * Nếu tất cả client đều dùng CHUNG một secret key để ký JWT, thì có thể dùng
					 * verifyWith() để xác minh chữ ký của tất cả các client
					 */
					.verifyWith(secretKeySpec).build()
					// Thực hiện parsing token:
					// - Giải mã phần header và payload (dạng Base64).
					// - Kiểm tra tính hợp lệ của chữ ký (signature).
					// - Nếu token expired hoặc malformed → ném exception.
					// - Nếu thành công, trả về một đối tượng Jwt<Header, Claims>.
					.parseSignedClaims(token).getPayload();

		} catch (ExpiredJwtException e) {
			throw new JwtValidationException("Access token expired!", e);
		} catch (IllegalArgumentException e) {
			throw new JwtValidationException("Access token is illegal!", e);
		} catch (MalformedJwtException e) {
			throw new JwtValidationException("Access token is not well formed!", e);
		} catch (UnsupportedJwtException e) {
			throw new JwtValidationException("Access token is not supported!", e);
		}
	}
}
