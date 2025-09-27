package com.todaii.english.infra.security.jwt;

import org.springframework.security.core.AuthenticationException;

public class JwtValidationException extends AuthenticationException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public JwtValidationException(String message) {
		super(message);
	}

	public JwtValidationException(String message, Throwable cause) {
		super(message, cause);
	}

}
