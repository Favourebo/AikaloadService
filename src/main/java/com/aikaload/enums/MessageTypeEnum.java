package com.aikaload.enums;

public enum MessageTypeEnum {
    NEW_JOB(1, "NEW_JOB"),
    ASSIGN_JOB(2, "ASSIGN_JOB"),
    SHOW_INTEREST(3, "SHOW_INTEREST"),
    JOB_REVIEW(4,"JOB_REVIEW");

    int code;
    String message;

    MessageTypeEnum(int code, String message) {
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
