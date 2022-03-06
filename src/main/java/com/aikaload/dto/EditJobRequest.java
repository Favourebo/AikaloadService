package com.aikaload.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class EditJobRequest {
    @ApiModelProperty(name =  "jobId", dataType  = "Long", value = "Unique Identifier for the job (ensure a valid jobId is specified)", example = "28", required = true)
    private Long jobId;

    @ApiModelProperty(name =  "userId", dataType = "Long", value = "User editing job", example = "30", required = true)
    private Long userId;

    @ApiModelProperty(name =  "pickUpAddressState", dataType = "String", value = "state where load is to be picked up", example = "Lagos", required = false)
    private String pickUpAddressState;

    @ApiModelProperty(name =  "pickUpAddressCity", dataType = "String", value = "city where load is to be picked up", example = "Ikeja", required = false)
    private String pickUpAddressCity;

    @ApiModelProperty(name =  "dropOffAddressState", dataType = "String", value = "state where load is to be dropped off", example = "Lagos", required = false)
    private String dropOffAddressState;

    @ApiModelProperty(name =  "dropOffAddressCity", dataType = "String", value = "state where load is to be dropped off", example = "Agege", required = false)
    private String dropOffAddressCity;

    @ApiModelProperty(name =  "materialType", dataType = "Long", value = "provide ID of the material type ", example = "4", required = false)
    private Long materialType;

    @ApiModelProperty(name =  "loadLevel", dataType = "int", value = "provide ID of the load level", example = "1", required = false)
    private int loadLevel;

    //Send notification to truck owners based on truck type specified
    @ApiModelProperty(name =  "truckType", dataType = "int", value = "Provide ID of the truck type", example = "1", required = false)
    private int truckType;

    @ApiModelProperty(name =  "numberOfTrucks", dataType = "int", value = "contains number of trucks required", example = "3", required = false)
    private int numberOfTrucks;

    @ApiModelProperty(name =  "pickUpDate", dataType = "String", value = "contains date a load is to be picked up (MM/DD/YYYY) format", example = "10/17/2020", required = false)
    private String pickUpDate;

    @ApiModelProperty(name =  "jobSummary", dataType = "String", value = "contains a brief description about job being created", example = "Movement of tomatoes", required = false)
    private String jobSummary;

   @ApiModelProperty(name =  "jobStatus", dataType = "int", value = "PROMOTED(1),\n" +
            "    ACTIVE(2),\n" +
            "    ASSIGNED(3),\n" +
            "    CLOSED(4),\n" +
            "    CANCELLED(5);", example = "2", required = false)
    private int jobStatus;
}
