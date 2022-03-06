package com.aikaload.entity;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity(name="driver_info")
@Getter
@Setter
@NoArgsConstructor
public class DriverInfo {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="driver_name")
	private String driverName;
	
	@Column(name="email_address")
	private String emailAddress;
	
	@Column(name="license_number")
	private String licenseNumber;
	
	@Column(name="address")
	private String address;
	
	@Column(name="mobile_number")
	private String mobileNumber;
	
	@Column(name="driver_license")
	private Byte[] driverLicense;
	
	@Column(name="fidelity_insurance_possible_claim_amount")
	private String fidelityInsurancePossibleClaimAmount;
	
	@Column(name="fidelity_insurance_issuer")
	private String fidelityInsuranceIssuer;
	
	@Column(name="fidelity_insurance_expiry_date")
	private Date fidelityInsuranceExpiryDate;
	
	@ManyToOne
	@JoinColumn(name = "user_account_id")
	private UserAccount userAccount;
	
	private int isVerified;
}
