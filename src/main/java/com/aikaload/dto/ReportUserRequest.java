package com.aikaload.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ReportUserRequest {
    @ApiModelProperty(name =  "reporterId", dataType = "Long", value = "User filing report", example = "29", required = true)
    private Long reporterId;

    @ApiModelProperty(name =  "userReportedId", dataType = "Long", value = "User being reported", example = "31", required = true)
    private Long userReportedId;

    @ApiModelProperty(name =  "message", dataType = "String", value = "What the user is complaining about", example = "He doesn't get the job done on time", required = true)
    private String comment;
}
