package com.aikaload.enums;

public enum TruckEnum {
    AVAILABLE(1, "AVAILABLE"),
    UNAVAILABLE(2, "UNAVAILABLE"),
    REMOVED(3, "REMOVED");

    int code;
    String message;

    TruckEnum(int code, String message) {
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
