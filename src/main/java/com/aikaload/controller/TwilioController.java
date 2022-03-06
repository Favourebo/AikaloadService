package com.aikaload.controller;

import com.aikaload.dto.TwilioWebhookBody;
import com.aikaload.service.TwilioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/twilio")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class TwilioController {

    private final TwilioService twilioService;

    @GetMapping("/get-twilio-token/{username}")
    public ResponseEntity<Object> getTwilioToken(@PathVariable String username){
        return twilioService.createTwilioToken(username);
    }

    @PostMapping(value="/pre-web-hook-w1234ft",  produces = {MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.APPLICATION_JSON_VALUE}, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<Object> twilioPreWebHook(TwilioWebhookBody twilioWebhookBody){
        return twilioService.twilioPreWebHook(twilioWebhookBody);
    }
}
