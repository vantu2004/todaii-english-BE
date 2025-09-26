package com.todaii.english.shared.enums.error_code;

import lombok.Getter;

@Getter
public enum AdminErrorCode {
    ADMIN_NOT_FOUND(404, "Admin not found"),
    ADMIN_ALREADY_EXISTS(409, "Admin already exists"),
    ADMIN_FORBIDDEN(403, "Admin does not have permission"),
    ROLE_NOT_FOUND(404, "Role not found");

    private final int status;
    private final String message;

    AdminErrorCode(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
