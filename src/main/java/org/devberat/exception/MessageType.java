package org.devberat.exception;

import lombok.Getter;

@Getter
public enum MessageType {
    NO_RECORD_EXIST("1001", "Record not found"),
    USER_ALREADY_EXISTS("1002", "This user is already registered"),
    UNAUTHORIZED("4001", "Authentication required"),
    ACCESS_DENIED("4003", "ADMIN authority is required for this operation"),
    GENERAL_EXCEPTION("9999", "A system error occurred");

    private final String code;
    private final String message;

    MessageType(String code, String message) {
        this.code = code;
        this.message = message;
    }
}