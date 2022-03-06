package com.aikaload.utils;

import lombok.Data;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class MailUtils {

    /**
     * Information to be injected into the template of the app
     */
    private Map<String, Object> mailProps;

    private String htmlFileName;

    private String to;

    private String name;

    private String subject;

    public MailUtils() {

    }
}
