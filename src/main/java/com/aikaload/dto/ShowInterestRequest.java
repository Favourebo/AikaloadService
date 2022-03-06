package com.aikaload.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ShowInterestRequest {
    @ApiModelProperty(name =  "encryptedRequest", dataType = "String", value = "required for UI purpose", required = false)
    private String encryptedRequest;

    @ApiModelProperty(name =  "offerAmountPerTruck", dataType = "String", value = "amount to be paid for one truck", example = "2000.00", required = true)
    private String offerAmount;

    @ApiModelProperty(name =  "numOfTrucks", dataType = "Integer", value = "number of trucks", example = "2", required = true)
    private int numOfTrucks;

    @ApiModelProperty(name =  "truckOwnerId", dataType = "Long", value = "User indicating interest", example = "32", required = true)
    private Long truckOwnerId;

    @ApiModelProperty(name =  "truckId", dataType = "Integer", value = "a list of truck IDs", example = "[25]", required = true)
    private int[] truckId;

    @ApiModelProperty(name =  "expectedDeliveryDays", dataType = "Integer", value = "number of days required for delivery", example = "3", required = true)
    private int expectedDeliveryDays;

    @ApiModelProperty(name =  "jobId", dataType = "Long", value = "unique identifier for job", example = "112", required = true)
    private Long jobId;

}
