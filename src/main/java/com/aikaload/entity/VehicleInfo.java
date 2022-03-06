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

@Entity(name="vehicle_info")
@Getter
@Setter
@NoArgsConstructor
public class VehicleInfo {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="vehicle_plate_number")
	private String vehiclePlateNumber;
	
	@Column(name="vehicle_model_name")
	private String vehicleModelName;
	
	@Column(name="vehicle_model_year")
	private String vehicleModelYear;
	
	@Column(name="git_insurance_possible_claim_amount")
	private String gitInsurancePossibleClaimAmount;
	
	@Column(name="git_insurance_issuer")
	private String gitInsuranceIssuer;
	
	@Column(name="git_insurance_expiry_date")
	private Date   gitInsuranceExpirDate;
	
	@Column(name="motor_insurance_possible_claim_amount")
	private String motorInsurancePossibleClaimAmount;
	
	@Column(name="motor_insurance_issuer")
	private String motorInsuranceIssuer;
	
	@Column(name="motor_insurance_expiry_date")
	private Date motorInsuranceExpirDate;
	
	@Column(name="vehicle_capacity")
	private float vehicleCapacity;
	
	@Column(name="vehicle_capacity_unit")
	private String vehicleCapacityUnit;
	
	@Column(name="vehicle_length")
	private float vehicleLength;
	
	@Column(name="means_of_identification")
	private Byte[] meansOfIdentification;
	
	@Column(name="contact_person_address_utility_bill")
	private Byte[] contactPersonAddressUtilityBill;
	
	@Column(name="company_address_utility_bill")
	private Byte[] companyAddressUtilityBill;
	
	@Column(name="driver_license")
	private Byte[] driverLicense;
	
	@Column(name="vehicle_photo")
	private Byte[] vehiclePhoto;
	
	@ManyToOne
	@JoinColumn(name = "user_account_id")
	private UserAccount userAccount;
	
	@Column(name = "is_verified")
	private int isVerified;

}
