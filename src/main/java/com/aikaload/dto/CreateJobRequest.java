package com.aikaload.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CreateJobRequest {

    @ApiModelProperty(name =  "userId", dataType = "Long", value = "User creating job", example = "29", required = true)
    private Long userId;

    @ApiModelProperty(name =  "pickUpAddressState", dataType = "String", value = "state where load is to be picked up", example = "Lagos", required = true)
    private String pickUpAddressState;

    @ApiModelProperty(name =  "pickUpAddressCity", dataType = "String", value = "city where load is to be picked up", example = "Ikeja", required = true)
    private String pickUpAddressCity;

    @ApiModelProperty(name =  "dropOffAddressState", dataType = "String", value = "state where load is to be dropped off", example = "Lagos", required = true)
    private String dropOffAddressState;

    @ApiModelProperty(name =  "dropOffAddressCity", dataType = "String", value = "state where load is to be dropped off", example = "Agege", required = true)
    private String dropOffAddressCity;

    @ApiModelProperty(name =  "materialType", dataType = "Long", value = "provide ID of the material type ", example = "4", required = true)
    private Long materialType;

    @ApiModelProperty(name =  "loadLevel", dataType = "int", value = "provide ID of the load level", example = "1", required = true)
    private int loadLevel;


    //Send notification to truck owners based on truck type specified
    @ApiModelProperty(name =  "truckType", dataType = "int", value = "Provide ID of the truck type", example = "1", required = true)
    private int truckType;

    @ApiModelProperty(name =  "numberOfTrucks", dataType = "int", value = "contains number of trucks required", example = "3", required = true)
    private int numberOfTrucks;

    @ApiModelProperty(name =  "pickUpDate", dataType = "String", value = "contains date a load is to be picked up (MM/DD/YYYY) format", example = "10/17/2020", required = true)
    private String pickUpDate;

    @ApiModelProperty(name =  "jobSummary", dataType = "String", value = "contains a brief description about job being created", example = "Movement of tomatoes", required = true)
    private String jobSummary;

}
