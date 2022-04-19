package com.aikaload.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TruckRequest {
    private int truckId;
    private String verificationStatus;
    private int truckModelId;
    private int truckTypeId;
    private String truckYear;
    private String plateNumber;
    //one for AVAILABLE, two for NOT AVAILABLE
    private int truckStatus;
    private String[] truckPictures;
    private Long createdBy;
    private String description;
}
