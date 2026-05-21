package com.todaii.english.client;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;

import com.todaii.english.client.security.CustomUserDetails;

public class UserUtils {
  public static Long getCurrentUserId(Authentication authentication) {
    if (authentication == null) {
      throw new AuthenticationCredentialsNotFoundException("User is not authenticated");
    }

    CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
    return customUserDetails.getUser().getId();
  }
}
