package com.todaii.english.server.security;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.todaii.english.infra.security.jwt.CustomHeaderRequestWrapper;
import com.todaii.english.shared.constants.SecurityConstants;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AdminCookieAuthFilter extends OncePerRequestFilter {

	private static final String COOKIE_NAME = "admin_access_token";

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {

		if (request.getCookies() != null) {
			for (Cookie cookie : request.getCookies()) {
				if (COOKIE_NAME.equals(cookie.getName())) {
					request = new CustomHeaderRequestWrapper(request, SecurityConstants.HEADER_STRING,
							SecurityConstants.TOKEN_PREFIX + cookie.getValue());
					break;
				}
			}
		}

		chain.doFilter(request, response);
	}
}
