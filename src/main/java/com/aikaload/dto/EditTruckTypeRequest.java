package com.aikaload.dto;

import lombok.Data;

@Data
public class EditTruckTypeRequest {
    private int truckTypeId;
    private String truckName;
}
