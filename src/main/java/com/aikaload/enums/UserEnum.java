package com.aikaload.enums;

public enum UserEnum {
    VERIFIED(1, "VERIFIED"),
    ACTIVE(2, "ACTIVE"),
    DISABLED(3, "DISABLED");

    int code;
    String message;

    UserEnum(int code, String message) {
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
