package com.todaii.english.shared.exceptions.user;

public class UserNotfoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UserNotfoundException() {
		super("User not found.");
	}
}
