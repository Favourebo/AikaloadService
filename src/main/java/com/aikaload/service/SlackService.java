package com.aikaload.service;

import com.aikaload.asyncservice.SmsService;
import com.aikaload.dto.Response;
import com.aikaload.enums.ResponseEnum;
import com.aikaload.utils.VariableUtil;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Log4j2
@Service("SlackService")
@AllArgsConstructor
public class SlackService {
    private final SmsService smsService;

    public ResponseEntity<Response> sendMessageToAdmin(String message){
        if(StringUtils.isNotBlank(message)){
            log.info("message:{}",message);
            smsService.sendAdminPushNotification(message, VariableUtil.ADMIN_SLACK_URL);
            return ResponseEntity.ok().body(new Response(ResponseEnum.OK.getCode(),"Successful",null));
        }
        return ResponseEntity.badRequest().body(new Response(ResponseEnum.INVALID_VALUE.getCode(),"Invalid value supplied",null));
    }
}
