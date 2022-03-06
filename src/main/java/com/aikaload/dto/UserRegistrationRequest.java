package com.aikaload.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserRegistrationRequest {
    @ApiModelProperty(name =  "companyName", dataType = "String", value = "Testco", required = true)
    private String companyName;

    @ApiModelProperty(name =  "mobilePhone", dataType = "String", value = "08060000000", required = true)
    private String mobilePhone;

    @ApiModelProperty(name =  "firstName", dataType = "String", value = "John", required = true)
    private String firstName;

    @ApiModelProperty(name =  "lastName", dataType = "String", value = "Doe", required = true)
    private String lastName;

    @ApiModelProperty(name =  "password", dataType = "String", value = "Enter a valid password", required = true)
    private String password;

    @ApiModelProperty(name =  "username", dataType = "String", value = "Enter a valid username", required = true)
    private String username;

    @ApiModelProperty(name =  "userRoleId", dataType = "Long", value = "1", required = true)
    private long userRoleId;
}
