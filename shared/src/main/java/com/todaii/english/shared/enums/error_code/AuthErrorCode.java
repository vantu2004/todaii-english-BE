package com.todaii.english.shared.enums.error_code;

import lombok.Getter;

@Getter
public enum AuthErrorCode {
    PASSWORD_INVALID_LENGTH(400, "Password length must be between 6 and 20"),
    PASSWORD_INCORRECT(400, "Password is incorrect"),
    TOKEN_EXPIRED(401, "Token expired"),
    INVALID_TOKEN(401, "Invalid token");
    
    private final int status;
    private final String message;

    AuthErrorCode(int status, String message) {
        this.status = status;
        this.message = message;
    }
}

