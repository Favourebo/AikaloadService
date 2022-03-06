package com.aikaload.controller;

import com.aikaload.dto.AccessTokenRequest;
import com.aikaload.service.AccessTokenService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


/**
 * <p>
 * This class contains methods that processes authentication request from the caller
 * @author Favour Ebo
 * created on 2020/07/25
 * @version 1.0
 */
@RestController
@RequestMapping("/auth")
@AllArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AuthenticationController{

    private final AccessTokenService accessTokenService;

    @PostMapping("/token")
    public ResponseEntity generateAccessToken(@RequestBody AccessTokenRequest accessTokenRequest, HttpServletRequest servletRequest){
      return accessTokenService.generateAccessToken(accessTokenRequest,servletRequest);
   }

}
