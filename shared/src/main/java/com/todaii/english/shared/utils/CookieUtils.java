package com.todaii.english.shared.utils;
import org.springframework.http.ResponseCookie;

public class CookieUtils {
	private static final String ACCESS_TOKEN_NAME = "access_token";
	private static final String REFRESH_TOKEN_NAME = "refresh_token";

	// Tokens duration
    private static final long ACCESS_TOKEN_EXPIRY = 15 * 60; // 15 minutes
    private static final long REFRESH_TOKEN_EXPIRY = 7 * 24 * 60 * 60; // 7 days
	
    private static final boolean SECURE = false; // use HTTPS in production
    private static final String PATH = "/"; // apply to all routes
    
    public static ResponseCookie createAccessTokenCookie(String token) {
    	return ResponseCookie.from(ACCESS_TOKEN_NAME, token)
    			.httpOnly(true) // accessible through JS flag (document.cookie)
    			.secure(SECURE) // through HTTPS only flag
    			.path(PATH)
    			.maxAge(ACCESS_TOKEN_EXPIRY)
    			//.sameSite("None")
    			.build();
    }
    
    public static ResponseCookie createRefreshTokenCookie(String token) {
    	return ResponseCookie.from(REFRESH_TOKEN_NAME, token)
    			.httpOnly(true) // accessible through JS flag (document.cookie)
    			.secure(SECURE) // through HTTPS only flag
    			.path(PATH)
    			.maxAge(REFRESH_TOKEN_EXPIRY)
    			//.sameSite("None")
    			.build();
    }
    
    public static ResponseCookie removeCookie(String cookieName) {
    	return ResponseCookie.from(cookieName, null)
    			.httpOnly(true)
    			.secure(SECURE)
    			.path(PATH)
    			.maxAge(0)
    			//.sameSite("None")
    			.build();
    }
    
}
