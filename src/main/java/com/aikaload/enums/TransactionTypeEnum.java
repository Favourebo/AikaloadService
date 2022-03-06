package com.aikaload.enums;

public enum TransactionTypeEnum {
    CREDIT("CR"),
    DEBIT("DR");

    String message;

    TransactionTypeEnum(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
