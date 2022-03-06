package com.aikaload.entity;

import lombok.Data;
import javax.persistence.*;
import java.math.BigDecimal;

@Data
@Entity
@Table(name="fee_configuration")
public class FeeConfiguration{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="verification_cost")
    private BigDecimal verificationCost;

    @Column(name="sms_cost", columnDefinition="Decimal(10,2) default '4.00'")
    private BigDecimal smsCost;
}
