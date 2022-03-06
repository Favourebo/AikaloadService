package com.aikaload.enums;

public enum AdminSettlementStatusEnum {
    PENDING(1, "PENDING"),
    STARTED(2, "STARTED"),
    COMPLETED(3, "COMPLETED");

    int code;
    String message;

    AdminSettlementStatusEnum(int code, String message) {
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
