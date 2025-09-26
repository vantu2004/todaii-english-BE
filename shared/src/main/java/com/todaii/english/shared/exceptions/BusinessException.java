package com.todaii.english.shared.exceptions;

import com.todaii.english.shared.enums.error_code.AdminErrorCode;
import com.todaii.english.shared.enums.error_code.AuthErrorCode;
import com.todaii.english.shared.enums.error_code.CommonErrorCode;
import com.todaii.english.shared.enums.error_code.UserErrorCode;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final Enum<?> errorCode; // chứa cả CommonErrorCode, UserErrorCode, AdminErrorCode...

	public BusinessException(Enum<?> errorCode) {
		super(errorCode.toString());
		this.errorCode = errorCode;
	}

	public int getStatus() {
		if (errorCode instanceof CommonErrorCode e)
			return e.getStatus();
		if (errorCode instanceof UserErrorCode e)
			return e.getStatus();
		if (errorCode instanceof AdminErrorCode e)
			return e.getStatus();
		if (errorCode instanceof AuthErrorCode e)
			return e.getStatus();
		return 500;
	}

	public String getMessageText() {
		if (errorCode instanceof CommonErrorCode e)
			return e.getMessage();
		if (errorCode instanceof UserErrorCode e)
			return e.getMessage();
		if (errorCode instanceof AdminErrorCode e)
			return e.getMessage();
		if (errorCode instanceof AuthErrorCode e)
			return e.getMessage();
		return "Unknown error";
	}
}
