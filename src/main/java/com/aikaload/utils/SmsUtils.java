package com.aikaload.utils;

import lombok.Data;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Data
public class SmsUtils{
    private String name;
    private String sender;
    private String message;
    private List<Object> contacts;
    private String[] type;

    public SmsUtils(String name,String message,String phone,String countryId){
        Map<String,String> contact = new HashMap<>();
        contact.put("phone",phone);
        contact.put("countryId",countryId);

        List<Object> contactList = new ArrayList<>();
        contactList.add(contact);

        this.name = name;
        this.message = message;
        this.contacts = contactList;
    }
}
