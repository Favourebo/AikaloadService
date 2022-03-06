package com.aikaload.dto;

import lombok.Data;

@Data
public class TwilioWebhookBody {
    private String EventType;
    private String ChannelSid;
    private String Body;
    private String Attributes;
    private String from;
}
