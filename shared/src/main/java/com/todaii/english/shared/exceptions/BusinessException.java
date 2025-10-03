package com.todaii.english.shared.exceptions;

import com.todaii.english.shared.enums.error_code.ErrorCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private final int status;
	private final String message;

	// Dùng enum implement ErrorCode
	public BusinessException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.status = errorCode.getStatus();
		this.message = errorCode.getMessage();
	}

	// Custom (không dùng enum)
	public BusinessException(int status, String message) {
		super(message);
		this.status = status;
		this.message = message;
	}
}
