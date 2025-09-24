package com.todaii.english.shared.enums;

import lombok.Getter;

@Getter
public enum ErrorCode {
	// Mỗi phần tử trong enum thực chất là một instance (object) của enum đó.
	USER_NOT_FOUND(404, "User not found"), USER_ALREADY_EXISTS(409, "User already exists"),
	INVALID_TOKEN(401, "Invalid token");

	private final int status;
	private final String message;

	// Lombok không tạo constructor cho enum, phải khai báo thủ công
	ErrorCode(int status, String message) {
		this.status = status;
		this.message = message;
	}
}
