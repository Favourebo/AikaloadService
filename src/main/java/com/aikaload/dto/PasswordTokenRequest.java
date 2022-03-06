package com.aikaload.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PasswordTokenRequest {
    @ApiModelProperty(name =  "password", dataType = "String", value = "user's password", required = true)
    private String password;

    @ApiModelProperty(name =  "userId", dataType = "Long", value = "id of the user", required = true)
    private Long userId;
}
