package com.todaii.english.shared.utils;

import org.springframework.http.ResponseCookie;

import com.todaii.english.shared.constants.SecurityConstants;

public class CookieUtils {
	private static final String ACCESS_TOKEN_NAME = "access_token";
	private static final String REFRESH_TOKEN_NAME = "refresh_token";

	// Tokens duration
	private static final long ACCESS_TOKEN_EXPIRY = SecurityConstants.ACCESS_TOKEN_EXPIRATION_MINUTES;
	private static final long REFRESH_TOKEN_EXPIRY = SecurityConstants.REFRESH_TOKEN_EXPIRATION_MINUTES;

	private static final boolean SECURE = false;
	private static final String PATH = "/";

	public static ResponseCookie createAccessTokenCookie(String token, String userType) {
		String name = userType + "_" + ACCESS_TOKEN_NAME;

		return ResponseCookie.from(name, token)
				// Cookie chỉ được gửi qua HTTP request, JavaScript không thể đọc nó.
				.httpOnly(true)
				/*
				 * Cookie chỉ được gửi qua kết nối HTTPS (bảo mật SSL/TLS), hiện tại đang dev
				 * nên đặt false.
				 */
				.secure(SECURE)
				// Cookie được gửi cho mọi endpoint của domain
				.path(PATH).maxAge(ACCESS_TOKEN_EXPIRY)
				/*
				 * dùng None khi FE/BE chạy khác domain, mặc dù đang dev và khác domain rồi
				 * nhưng nó lại chỉ dùng khi gửi qua HTTPS -> tắt
				 */
				// .sameSite("None")
				.build();
	}

	public static ResponseCookie createRefreshTokenCookie(String token, String userType) {
		String name = userType + "_" + REFRESH_TOKEN_NAME;

		return ResponseCookie.from(name, token).httpOnly(true).secure(SECURE).path(PATH).maxAge(REFRESH_TOKEN_EXPIRY)
				// .sameSite("None")
				.build();
	}

	public static ResponseCookie removeCookie(String cookieName) {
		return ResponseCookie.from(cookieName, null).httpOnly(true).secure(SECURE).path(PATH).maxAge(0)
				// .sameSite("None")
				.build();
	}

}
