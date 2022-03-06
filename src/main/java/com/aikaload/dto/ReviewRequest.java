package com.aikaload.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ReviewRequest {

    @ApiModelProperty(name =  "revieweeId", dataType = "Long", value = "Id of the user being reviewed", example = "41", required = true)
    private Long revieweeId;

    @ApiModelProperty(name =  "reviewerId", dataType = "Long", value = "Id of the user who is reviewing", example = "40", required = true)
    private Long reviewerId;

    @ApiModelProperty(name =  "comment", dataType = "String", value = "review message", example = "User A handles delivery excellently", required = true)
    private String comment;
}
