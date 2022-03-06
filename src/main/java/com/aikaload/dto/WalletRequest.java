package com.aikaload.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class WalletRequest {
    @ApiModelProperty(name="transactionAmount", dataType = "Decimal", value = "Amount user is funding wallet with", example = "200.00", required = true)
    private BigDecimal transactionAmount;

    @ApiModelProperty(name="transactionReference", dataType = "String", value = "Value must be in the format (yymmddhhmmss)", example = "202009328110223", required = true)
    private String transactionReference;

    @ApiModelProperty(name="userId", dataType = "Long", value = "User Funding wallet", example = "39", required = true)
    private Long userId;

    public WalletRequest(BigDecimal transactionAmount, String transactionReference, Long userId){
        this.transactionAmount = transactionAmount;
        this.transactionReference = transactionReference;
        this.userId = userId;
    }
}
