package com.aikaload.host;

import com.aikaload.dto.AccessTokenRequest;
import com.aikaload.dto.Response;
import com.aikaload.enums.ResponseEnum;
import lombok.extern.log4j.Log4j2;
import org.apache.tomcat.util.codec.binary.Base64;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
@Log4j2
public class RestConnector {

    private static final String HTTP_STATUS_CODE_EXCEPTION = "HttpStatusCodeException::";

    public ResponseEntity sendAccessTokenRequest(String url, AccessTokenRequest accessTokenRequest){
        Map<String,Object> data;
        JSONObject resultJsonObject;
        try{
            String result;
            //Setup basic auth credentials
            String plainCreds = accessTokenRequest.getClientId()+":"+accessTokenRequest.getClientSecret();
            byte[] plainCredsBytes = plainCreds.getBytes();
            byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
            String base64Creds = new String(base64CredsBytes);

            //Setup HttpHeader
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", "Basic " + base64Creds);
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);


            MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
            map.add("grant_type", accessTokenRequest.getGrantType());
            map.add("username", accessTokenRequest.getUsername());
            map.add("password", accessTokenRequest.getPassword());

            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

            //Setup Rest template
            RestTemplate restTemplate = new RestTemplate();
            log.info("<<<<<<<POST URL:{}", url);

            //Send request with POST method, and Headers.
            ResponseEntity<String> responseFromServer = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            result = responseFromServer.getBody();

            log.info("<<<<<<<POST RESPONSE  :: STATUS CODE::"+responseFromServer.getStatusCode()+"::Response Body::" + result);
            resultJsonObject = new JSONObject(result);

            data = new HashMap<>();
            data.put("access_token", resultJsonObject.getString("access_token"));
            data.put("expires_in", resultJsonObject.getInt("expires_in"));
            data.put("scope", resultJsonObject.getString("scope"));
            data.put("jti", resultJsonObject.getString("jti"));
            return ResponseEntity.status(responseFromServer.getStatusCode()).body(new Response(ResponseEnum.OK.getCode(),ResponseEnum.OK.getMessage(),data));
        }
        catch(ResourceAccessException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Resource not found");
        }

        catch(HttpStatusCodeException exception){
            log.error(HTTP_STATUS_CODE_EXCEPTION + exception.getMessage());
            int statusCode = exception.getStatusCode().value();

            log.info("<<<<<POST RESPONSE::::" + exception.getResponseBodyAsString());
            resultJsonObject = new JSONObject(exception.getResponseBodyAsString());
            data = new HashMap<>();
            data.put("error",resultJsonObject.getString("error"));
            data.put("error_description",resultJsonObject.getString("error_description"));

            return ResponseEntity.status(statusCode).body(data);
        }

        catch(Exception e){
            log.error("<<<<<<<<<Exception occurred during posting request" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Response(ResponseEnum.AN_ERROR_OCCURRED.getCode(),"An error occurred",null));
        }
    }




    /**
     * Returns ResponseEntity<String> (contains response)  to calling client
     *  Request is the parameter that holds request to be sent to third party service
     *  URL is the url of the third party service
     *  ContentType is the format in which the request is to be sent
     *  Accept is the format in which the response is to be received.
     *
     * <p>
     * This method posts request to any third party service required.
     *
     * @param  url (url of the third party service
     * @param  request holds information to be posted to third party client service
     * @param  contentType format in which request should be sent
     * @param  accept format in which response should be received
     * @return the ResponseEntity<String>(which specifies whether the transaction was successful or not) of the transaction
     * @see ResponseEntity <String>
     */
    public ResponseEntity<String> sendPostRequest(String url, String request, MediaType contentType, MediaType accept) {

        ResponseEntity<String> responseFromServer;
        String result;

        log.info(">>>>>>>>>POST URL::::" + url);

        try{
            //Build header request
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(contentType);
            headers.setAccept(Collections.singletonList(accept));
            HttpEntity<String> entity = new HttpEntity<>(request, headers);

            RestTemplate restTemplate = new RestTemplate();
            log.info(">>>>>>>>>POST REQUEST::::" + request);

            //Send request with POST method, and Headers.
            responseFromServer = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            result = responseFromServer.getBody();

            log.info("<<<<<<<POST RESPONSE  :: STATUS CODE::"+responseFromServer.getStatusCode()+"::Response Body::" + result);
            return ResponseEntity.status(responseFromServer.getStatusCode()).body(result);
        }
        catch(ResourceAccessException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("");
        }

        catch(HttpStatusCodeException exception){
            log.error(HTTP_STATUS_CODE_EXCEPTION + exception.getMessage());
            int statusCode = exception.getStatusCode().value();

            log.info("<<<<<POST RESPONSE::::" + exception.getResponseBodyAsString());
            return ResponseEntity.status(statusCode).body(exception.getResponseBodyAsString());
        }

        catch(Exception e){
            log.error("<<<<<<<<<Exception occurred during posting request" + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("");
        }
    }

}
