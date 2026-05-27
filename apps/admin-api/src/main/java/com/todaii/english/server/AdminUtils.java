package com.todaii.english.server;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;

import com.todaii.english.server.security.CustomAdminDetails;

public class AdminUtils {
  public static Long getCurrentAdminId(Authentication authentication) {
    if (authentication == null) {
      throw new AuthenticationCredentialsNotFoundException("User is not authenticated");
    }

    CustomAdminDetails customAdminDetails = (CustomAdminDetails) authentication.getPrincipal();
    return customAdminDetails.getAdmin().getId();
  }

  public static String toAlias(String name) {
    return name.trim().toLowerCase().replaceAll("\\s+", "-");
  }

  public static String formatSearchKeyword(String keyword) {
    if (keyword == null || keyword.trim().isEmpty()) {
      return null;
    }
    return keyword.trim().replaceAll("\\s+", "%");
  }
}
