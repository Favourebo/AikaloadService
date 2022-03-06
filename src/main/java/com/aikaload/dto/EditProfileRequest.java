package com.aikaload.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EditProfileRequest {
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String companyName;
    private String routes;
    private String address;
    private String termsOfService;
    private String bio;
    private String userProfileUrl;
    private String state;
    private String country;
    private String whatsappNumber;
    private String city;
}
