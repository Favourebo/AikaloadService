package com.aikaload.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Entity
@Table(name="user_account")
@Getter
@Setter
@NoArgsConstructor
public class UserAccount {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "company_name")
	private String companyName;

	@Column(name="first_name")
	private String firstName;

	@Column(name="last_name")
	private String lastName;

	@Column(name="user_status",columnDefinition = "integer default 1")
	private int userStatus;

	@Column(name="routes")
	private String routes;

	@Column(name="state")
	private String state;

	@Column(name="country")
	private String country;

	@OneToMany(mappedBy = "assignedTo")
	@JsonIgnore
	private Set<AssignJobInfo> assignJobInfo;

	@Column(name="whatsapp_number")
	private String whatsappNumber;

	@Column(name="city")
	private String city;

	@Column(name="bio")
	private String bio;

	@Column(name="user_profile_url")
	private String userProfileUrl;

	@Column(name="terms_of_service")
	private String termsOfService;

	@Column(name = "contact_person_name")
	private String contactPersonName;

	@Column(name = "username")
	private String username;

	@Column(name = "password")
	@JsonIgnore
	private String password;

	@Column(name = "mobilephone")
	private String mobilePhone;

	@Column(name = "address_line_1")
	private String addressLine1;

	@Column(name = "address_line_2")
	private String addressLine2;

	@Column(name = "local_government")
	private String localGovernment;

	@Column(name = "wallet_balance", columnDefinition="Decimal(10,2) default '0.00'")
	private BigDecimal walletBalance;

	@Column(name = "postal_code")
	private String postalCode;

	@Column(name = "is_account_verified")
	private boolean isAccountVerified;

	@Column(name = "is_account_information_updated")
	private boolean isAccountInformationUpdated;

	@Column(name = "date_created")
	private Date dateCreated;

	@Column(name = "last_modified_date")
	private Date lastModifiedDate;

	@ManyToOne
	@JoinColumn(name = "user_role_id")
	private UserRole userRole;


	@Column(name = "bid_package_id",columnDefinition = "integer default 1")
	private long bidPackageId;


	@Column(name="smsNotification",  columnDefinition="boolean default false")
	private boolean smsNotification;


	@Column(name="show_whatsapp_number",  columnDefinition="boolean default false")
	private boolean showWhatsappNumber;

	@Column(name="show_phone_number",  columnDefinition="boolean default false")
	private boolean showPhoneNumber;

	@Column(name="whatsappNotification", columnDefinition="boolean default false")
	private boolean whatsappNotification;

	@Column(name="emailNotification", columnDefinition="boolean default true")
	private boolean emailNotification;


	@OneToMany(mappedBy = "reportFor")
	@JsonIgnore
	private Set<Notification> reportForNotification;


	@OneToMany(mappedBy = "userAccount")
	@JsonIgnore
	private Set<JobInfo> JobInfo;

	@OneToMany(mappedBy = "userAccount")
	@JsonIgnore
	private Set<UserRatingLog> userRatingLog;

	@OneToMany(mappedBy = "userAccount")
	@JsonIgnore
	private Set<VehicleInfo> vehicleInfo;

	@OneToMany(mappedBy = "userAccount")
	@JsonIgnore
	private Set<DriverInfo> driverInfo;

	@Transient
	@JsonIgnore
	private MultipartFile meansOfIdentificationRequest;

	@Lob
	@Column(name = "means_of_identification_doc", length = 100000)
	private byte[] meansOfIdentificationDoc;

	@Column(name = "has_active_bid_package_plan")
	private boolean hasActiveBidPackagePlan;

	@Column(name = "means_of_identification")
	private String meansOfIdentification;

	@Column(name = "utility_bill")
	private String utilityBill;

	@Lob
	@Column(name = "utility_bill_doc", length = 100000)
	private byte[] utilityBillDoc;

	@Column(name = "referred_by")
	private String referredBy;

	@Column(name = "num_of_ratings")
	private String noOfRatings;

	@Lob
	@Column(name = "profile_pix", length = 100000)
	private byte[] profilePicture;

	@Transient
	@JsonIgnore
	private MultipartFile utilityBillRequest;

	@OneToMany(mappedBy = "userAccount")
	@JsonIgnore
	private Set<TruckInfo> truckInfo;

	@Transient
	@JsonIgnore
	private String roleName;

	private String userRating;

}
