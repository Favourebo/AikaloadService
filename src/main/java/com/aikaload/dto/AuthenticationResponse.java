package com.aikaload.dto;

import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class AuthenticationResponse implements Serializable {
    private static final long serialVersionUID = 345L;

    private final String jwt;
    private int id;
    private String username;
    private List<String> roles;
    private String responseCode;
    private String responseMessage;

    public AuthenticationResponse(String jwt) {
        this.jwt = jwt;
    }
}
