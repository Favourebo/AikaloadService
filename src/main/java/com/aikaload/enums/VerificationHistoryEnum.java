package com.aikaload.enums;

public enum VerificationHistoryEnum {

    NOT_VERIFIED(0, "NOT_VERIFIED"),
    PENDING_VERIFICATION(1, "PENDING_VERIFICATION"),
    VERIFIED(2, "VERIFIED");

    int code;
    String message;

    VerificationHistoryEnum(int code, String message) {
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
