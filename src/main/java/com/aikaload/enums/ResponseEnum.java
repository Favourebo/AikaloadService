package com.aikaload.enums;

public enum ResponseEnum {

    OK("00", "Successful"),
    USER_REGISTRATION_FAILED("01", "User registration failed"),
    SYSTEM_ERROR("02", "System error"),
    VALUE_ALREADY_EXIST("03", "Already Exist"),
    INVALID_VALUE("04", "Invalid Value"),
    EMPTY_VALUE("05", "Empty Value"),
    OPERATION_FAILED("06", "Operation failed"),
    ALREADY_USED("07", "Already Used"),
    NO_RECORD_FOUND("25", "No record found"),
    INSUFFICIENT_BALANCE("51", "Insufficient balance, kindly fund wallet!"),
    SETTLEMENT_ERROR("52", "Settlement Error"),
    DUPLICATE_ENTRY("94", "Duplicate Entry"),
    AN_ERROR_OCCURRED("99", "Unable to process your request at the moment try again later");
    //=============
    String code;
    String message;

    ResponseEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
