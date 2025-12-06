package com.todaii.english.shared.enums.error_code;

import lombok.Getter;

@Getter
public enum CommonErrorCode implements ErrorCode {
	INVALID_REQUEST(400, "Invalid request"), VALIDATION_FAILED(422, "Validation failed"),
	UNAUTHORIZED(401, "Unauthorized"), FORBIDDEN(403, "Access denied"), NOT_FOUND(404, "Resource not found"),
	CONFLICT(409, "Conflict"), INTERNAL_ERROR(500, "Internal server error"),
	SERVICE_UNAVAILABLE(503, "Service temporarily unavailable");

	private final int status;
	private final String message;

	CommonErrorCode(int status, String message) {
		this.status = status;
		this.message = message;
	}

	@Override
    public int getStatus() {
        return this.status;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
