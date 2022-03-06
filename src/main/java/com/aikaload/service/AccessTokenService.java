package com.aikaload.service;

import com.aikaload.dto.AccessTokenRequest;
import com.aikaload.dto.Response;
import com.aikaload.entity.JobInfo;
import com.aikaload.enums.ResponseEnum;
import com.aikaload.host.RestConnector;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Comparator;

@Service
@Log4j2
@AllArgsConstructor
public class AccessTokenService {

    private final RestConnector restConnector;

    public ResponseEntity generateAccessToken(AccessTokenRequest accessTokenRequest, HttpServletRequest request){
        //Validate access token
        try {
            log.info(">>>>> Validating if accessTokenRequest is null");
            if (accessTokenRequest == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response(ResponseEnum.INVALID_VALUE.getCode(), "Request cannot be empty", null));
            }

            StringBuilder requestURL = new StringBuilder(request.getRequestURL().toString());
            return restConnector.sendAccessTokenRequest(requestURL.toString().replace("auth","oauth"), accessTokenRequest);
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;

    }





    public static void main(String args[]){
        System.out.println(2%16);


    }


}
