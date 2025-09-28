package com.todaii.english.infra.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {

	private static final Logger LOGGER = LoggerFactory.getLogger(JwtAuthEntryPoint.class);

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException {

		LOGGER.error("Unauthorized error: {}", authException.getMessage());

		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setStatus(HttpStatus.UNAUTHORIZED.value());

		Map<String, Object> body = new LinkedHashMap<String, Object>();
		body.put("timestamp", LocalDateTime.now().toString());
		body.put("status", HttpStatus.UNAUTHORIZED.value());
		body.put("error", HttpStatus.UNAUTHORIZED.getReasonPhrase());
		body.put("message", authException.getMessage() != null ? authException.getMessage() : "Unauthorized");
		body.put("path", request.getServletPath());

		new ObjectMapper().writeValue(response.getOutputStream(), body);
	}
}
