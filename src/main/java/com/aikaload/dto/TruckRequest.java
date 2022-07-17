package com.aikaload.dto;

import com.aikaload.enums.TransmissionEnum;
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
    private String[] truckVideos;
    private Long createdBy;
    private String description;
    private String truckName;
    private String location;
    private String truckSize;
    private TransmissionEnum transmission;
    private boolean isAirConditionerAvailable;
    private String longitude;
    private String latitude;
}
