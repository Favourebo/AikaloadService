package com.aikaload.asyncservice;

import com.pusher.rest.Pusher;
import com.pusher.rest.data.Result;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.Collections;


@Service
@Log4j2
public class PushNotificationService {

    private String channelName;

    @Value("${app.id}")
    private String appId;

    @Value("${key}")
    private String apiKey;

    @Value("${secret}")
    private String appSecret;

    @Value("${cluster}")
    private String appCluster;


     //WRITE_REVIEW_EVENT
     @Async("executorC")
     public void publishMessage(String message, String eventName,String channel){
        Pusher pusher = new Pusher(appId, apiKey, appSecret);
        pusher.setCluster(appCluster);
        Result result = pusher.trigger(channel, eventName, Collections.singletonMap("message", message));
        log.info("{}/{}/{}",result.getHttpStatus(),result.getMessage(),result.getStatus());
     }

}
