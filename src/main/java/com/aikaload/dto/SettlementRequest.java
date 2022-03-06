package com.aikaload.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SettlementRequest {
    @ApiModelProperty(name =  "loadCode", dataType = "String", value = "provided at the start of a job", example = "4937UEDY", required = false)
    private String loadCode;

    @ApiModelProperty(name =  "completionCode", dataType = "String", value = "provided at the end of a job", example = "3234OYIE", required = false)
    private String completionCode;

    @ApiModelProperty(name =  "truckOwnerId", dataType = "Long", value = "a number that uniquely identifies a truckOwner", example = "24", required = true)
    private Long truckOwnerId;

    @ApiModelProperty(name =  "jobId", dataType = "Long", value = "a number that uniquely identifies a job", example = "20", required = true)
    private Long jobId;
}
