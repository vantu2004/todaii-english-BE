package com.todaii.english.shared.enums.error_code;

import lombok.Getter;

@Getter
public enum AuthErrorCode implements ErrorCode {
	PASSWORD_INVALID_LENGTH(400, "Password length must be between 6 and 20"),
	PASSWORD_INCORRECT(400, "Password is incorrect"), TOKEN_EXPIRED(401, "Token expired"),
	TOKEN_NOT_FOUND(404, "Token not found"), INVALID_TOKEN(401, "Invalid token"), OTP_INVALID(401, "Otp invalid"),
	OTP_NOT_FOUND(404, "Otp not found"), OTP_EXPIRED(401, "Otp expired"), ALREADY_VERIFIED(400, "Already verified"),
	USER_NOT_ENABLED(403, "User not enabled");

	private final int status;
	private final String message;

	AuthErrorCode(int status, String message) {
		this.status = status;
		this.message = message;
	}
}
