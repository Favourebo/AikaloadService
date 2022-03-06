package com.aikaload.dto;

import lombok.Data;

@Data
public class VerificationDetailsRequest {
    private String[] youVerifyIds;
    private String bankName;
    private String bankAccountName;
    private String accountNumber;
    private String userId;
}
