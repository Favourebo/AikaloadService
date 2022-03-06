package com.aikaload.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class CloseTaskRequest{

    @ApiModelProperty(name =  "jobId", dataType  = "Long", value = "Unique Identifier for the job (ensure a valid jobId is specified)", example = "28", required = true)
    private Long jobId;

    @ApiModelProperty(name =  "truckId", dataType  = "Integer", value = "Unique Identifier for the truck (ensure a valid truckId is specified)", example = "14", required = true)
    private int truckId;

    //Optional
    @ApiModelProperty(name =  "comment", dataType = "String", value = "contains reviewer's comment", example = "Excellent service was provided", required = false)
    private String comment;

    //Optional
    @ApiModelProperty(name =  "userRating", dataType = "int", value = "takes in a range of 0-5 (its 0 by default)", example = "5", required = false)
    private int userRating;

    //Mandatory
    @ApiModelProperty(name =  "userId", dataType = "Long", value = "user who is being reviewed", example = "29", required = true)
    private Long userId;

    //Optional
    @ApiModelProperty(name =  "isTruckOwnerFavourite", dataType = "boolean", value = "specifies is a truck owner is picked as favourite or not (default is false)",
            example = "false", required = false)
    private boolean isTruckOwnerFavourite;

    //Optional
    @ApiModelProperty(name =  "dealAmount", dataType = "BigDecimal", value = "amount paid for the job", example = "2000.00", required = true)
    private BigDecimal dealAmount;
}

