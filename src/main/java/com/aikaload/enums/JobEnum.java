package com.aikaload.enums;

public enum JobEnum {
    PROMOTED(1, "PROMOTED"),
    ACTIVE(2, "ACTIVE"),
    ASSIGNED(3, "ASSIGNED"),
    CLOSED(4, "CLOSED"),
    CANCELLED(5, "CANCELLED"),
    PENDING_APPROVAL(6, "PENDING_APPROVAL");

    int code;
    String message;

    JobEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
