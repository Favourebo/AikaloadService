package com.aikaload.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity(name="audit_trail")
public class AuditTrail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String operation;

    @Column(length=10000)
    private String request;

    private Date transactionDate;


    public AuditTrail(String username, String operation, String request, Date transactionDate){
        this.username = username;
        this.operation = operation;
        this.request = request;
        this.transactionDate = transactionDate;
    }

    public AuditTrail(){}
}
