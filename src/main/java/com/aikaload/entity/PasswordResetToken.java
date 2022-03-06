package com.aikaload.entity;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Entity(name="password_reset_token")
@Getter
@Setter
@NoArgsConstructor
public class PasswordResetToken {
  
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
  
    private String token;
  
    @OneToOne(targetEntity = UserAccount.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_account_id")
    private UserAccount userAccount;
  
    private Date expiryDate;

    
    public PasswordResetToken(String token, UserAccount userAccount, Date expiryDate) {
		super();
		this.token = token;
		this.userAccount = userAccount;
		this.expiryDate = expiryDate;
	}
    
}