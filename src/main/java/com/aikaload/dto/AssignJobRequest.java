package com.aikaload.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AssignJobRequest {

    @ApiModelProperty(name =  "truckOwnerId", dataType = "Long", value = "contains a  user who have shown interest (NOTE: job cannot be assigned to a truck owner who has not indicated interest", example = "[32]", required = true)
    private Long truckOwnerId;

    @ApiModelProperty(name =  "jobId", dataType = "Long", value = "unique identifier for a job", example = "21", required = true)
    private Long jobId;

    @ApiModelProperty(name =  "numOfTrucks", dataType = "int", value = "number of trucks needed", example = "2", required = true)
    private int numOfTrucks;
}
