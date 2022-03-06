package com.aikaload.entity;

import lombok.Data;
import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="message_log")
@Data
public class MessageLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="logger_id")
    private Long loggerId;

    @Column(name="recipient_id")
    private Long recipientId;

    @Column(name="message",length=2000)
    private String message;

    @Column(name="message_type")
    private int messageType;

    @Column(name="created_date")
    private Date createdDate;
}
