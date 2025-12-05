package com.todaii.english.server;

import org.springframework.security.core.Authentication;

import com.todaii.english.server.security.CustomAdminDetails;

public class AdminUtils {
	public static Long getCurrentAdminId(Authentication authentication) {
		CustomAdminDetails customAdminDetails = (CustomAdminDetails) authentication.getPrincipal();
		return customAdminDetails.getAdmin().getId();
	}
}
