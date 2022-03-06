package com.aikaload.entity;

import lombok.Data;
import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="verification_history")
@Data
public class VerificationHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "verifiedBy")
    private Long verifiedBy;

    @Column(name = "verification_date")
    private Date verificationDate;

    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "verification_link")
    private String verificationLink;

    @Column(name="verification_status")
    private int verificationStatus;
}
