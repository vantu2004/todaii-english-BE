package com.todaii.english.shared.constants;

public class SecurityConstants {
	public static final int ACCESS_TOKEN_EXPIRATION_MINUTES = 15; // 15 phút
	public static final int REFRESH_TOKEN_EXPIRATION_MINUTES = 60 * 24 * 7; // 7 ngày
	public static final String TOKEN_PREFIX = "Bearer ";
	public static final String HEADER_STRING = "Authorization";
	public static final String SECRET_KEY_ALGORITHM = "HmacSHA512";
}
