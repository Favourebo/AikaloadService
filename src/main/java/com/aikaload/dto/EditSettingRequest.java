package com.aikaload.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class EditSettingRequest {

    @ApiModelProperty(name="userId", dataType = "Long", value = "User performing edit", example = "39", required = true)
    private Long userId;

    @ApiModelProperty(name="whatsappNotification", dataType = "boolean", value = "Can either be true or false", example = "false", required = true)
    private boolean whatsappNotification;

    @ApiModelProperty(name="smsNotification", dataType = "boolean", value = "Can either be true or false", example = "false", required = true)
    private boolean smsNotification;

    @ApiModelProperty(name="showWhatsappNumber", dataType = "boolean", value = "Can either be true or false", example = "false", required = true)
    private boolean showWhatsappNumber;

    @ApiModelProperty(name="showPhoneNumber", dataType = "boolean", value = "Can either be true or false", example = "false", required = true)
    private boolean showPhoneNumber;

    @ApiModelProperty(name="emailNotification", dataType = "boolean", value = "Can either be true or false", example = "true", required = true)
    private boolean emailNotification;
}
