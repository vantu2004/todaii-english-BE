package com.todaii.english.shared.enums.error_code;

import lombok.Getter;

@Getter
public enum UserErrorCode {
	USER_NOT_FOUND(404, "User not found"), USER_ALREADY_EXISTS(409, "User already exists");

	private final int status;
	private final String message;

	UserErrorCode(int status, String message) {
		this.status = status;
		this.message = message;
	}
}
