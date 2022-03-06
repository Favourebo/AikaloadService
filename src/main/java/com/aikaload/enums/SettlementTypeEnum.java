package com.aikaload.enums;

public enum SettlementTypeEnum {
    LOAD_CODE(1, "LOAD_CODE"),
    COMPLETION_CODE(2, "COMPLETION_CODE");

    int code;
    String message;

    SettlementTypeEnum(int code, String message) {
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
