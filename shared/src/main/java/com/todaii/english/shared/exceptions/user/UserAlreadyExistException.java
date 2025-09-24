package com.todaii.english.shared.exceptions.user;

public class UserAlreadyExistException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UserAlreadyExistException(String email) {
		super("User already exists with email: " + email);
	}
}
