package com.aikaload.entity;

import lombok.Data;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


@Data
@Entity
@Table(name="transaction_history")
public class TransactionHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="user_id")
    private Long userId;

    @Column(name="amount")
    private BigDecimal amount;

    @Column(name="transaction_ref")
    private String transactionRef;

    @Column(name="narration")
    private String narration;

    @Column(name="transaction_type")
    private String transactionType;

    @Column(name="transaction_date")
    private Date transactionDate;
}
