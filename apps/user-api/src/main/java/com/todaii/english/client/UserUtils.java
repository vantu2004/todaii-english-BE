package com.todaii.english.client;

import org.springframework.security.core.Authentication;

import com.todaii.english.client.security.CustomUserDetails;

public class UserUtils {
	public static Long getCurrentAdminId(Authentication authentication) {
		CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
		return customUserDetails.getUser().getId();
	}
}
