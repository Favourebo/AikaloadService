package com.aikaload.service;

import com.aikaload.asyncservice.SmsService;
import com.aikaload.dto.Response;
import com.aikaload.dto.TwilioWebhookBody;
import com.aikaload.enums.ResponseEnum;
import com.aikaload.utils.VariableUtil;
import com.twilio.Twilio;
import com.twilio.http.HttpMethod;
import com.twilio.jwt.accesstoken.AccessToken;
import com.twilio.jwt.accesstoken.ChatGrant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.net.URI;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class TwilioService {
    // Required for all types of tokens
    @Value("${twilio.account.sid}")
    private String twilioAccountSid;

    @Value("${twilio.sid}")
    private String twilioApiKey;

    @Value("${twilio.secret}")
    private String twilioApiSecret;

    // Required for IP Messaging
    @Value("${twilio.service.sid}")
    private String serviceSid;

    @Value("${twilio.auth.token}")
    private String twilioAuthToken;

    @Value("${service.context.root}")
    private String baseUrl;

    @Value("${twilio.pushcredentials.sid}")
    private String pushCredentialsSid;

    @Value("${twilio.forbidden.words}")
    private String forbiddenWord;

    @Value("${twilio.friendlyName}")
    private String friendlyName;

    private final SmsService smsService;

    Pattern pattern = Pattern.compile(VariableUtil.PHONE_NUMBER_REGEX_PATTERN);


    public ResponseEntity<Object> createTwilioToken(String identity) {
        // Create access token
        try {
            ChatGrant grant = new ChatGrant();
            grant.setServiceSid(serviceSid);
            grant.setPushCredentialSid(pushCredentialsSid);

            AccessToken token = new AccessToken.Builder(twilioAccountSid, twilioApiKey, twilioApiSecret)
                    .identity(identity).grant(grant).build();

            String jwtToken = token.toJwt();
            Twilio.init(twilioAccountSid, twilioAuthToken);

          /*com.twilio.rest.chat.v2.Service service = com.twilio.rest.chat.v2.Service.updater(serviceSid)
                    .setPreWebhookUrl(URI.create(String.format("%s/twilio/pre-web-hook-w1234ft",baseUrl)))
                    .setWebhookMethod(HttpMethod.POST)
                    .setReachabilityEnabled(true).update();*/

            return ResponseEntity.ok().body(new Response("00", "Successful", jwtToken));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok().body(new Response(ResponseEnum.AN_ERROR_OCCURRED.getCode(), ResponseEnum.AN_ERROR_OCCURRED.getMessage(), null));
        }
    }

    public ResponseEntity<Object> twilioPreWebHook(TwilioWebhookBody twilioWebhookBody) {
        if(twilioWebhookBody.getEventType().equalsIgnoreCase("onMessageSend")) {
            String[] forbiddenWords = forbiddenWord.split(",");

            // Validating if there's a phone number present
            if (pattern.matcher(twilioWebhookBody.getBody()).matches())
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("");


            //Validating that none of the forbidden words exist
            for (String forbiddenWord : forbiddenWords) {
                if (twilioWebhookBody.getBody().toLowerCase().contains(forbiddenWord.toLowerCase()))
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("");

                if (twilioWebhookBody.getBody().toLowerCase().contains("@")){
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("");
                }

                if(pattern.matcher(twilioWebhookBody.getBody()).matches())
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("");
            }
        }

        String message = String.format("New Message with Sid: %s from %s with attribute:%s",serviceSid,twilioWebhookBody.getFrom(),twilioWebhookBody.getAttributes());
        smsService.sendAdminPushNotification(message,VariableUtil.CREATE_CHAT_MESSAGE_SLACK_URL);
        return ResponseEntity.status(HttpStatus.OK).body("");
    }
}
