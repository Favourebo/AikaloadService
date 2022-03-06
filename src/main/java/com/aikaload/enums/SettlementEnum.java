package com.aikaload.enums;

public enum SettlementEnum {
    FRESH(1, "FRESH"),
    START_SETTLEMENT_REQUEST(2, "START_SETTLEMENT_REQUEST"),
    END_SETTLEMENT_REQUEST(3, "END_SETTLEMENT_REQUEST");

    int code;
    String message;

    SettlementEnum(int code, String message) {
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
