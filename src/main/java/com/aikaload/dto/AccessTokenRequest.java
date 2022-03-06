package com.aikaload.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccessTokenRequest {
    private String clientId;
    private String clientSecret;
    private String grantType;
    private String username;
    private String password;

}
