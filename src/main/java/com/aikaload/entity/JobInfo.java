package com.aikaload.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
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

@Entity(name="job_info")
@Getter
@Setter
@NoArgsConstructor
public class JobInfo {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="job_reference_number")
	private String jobReferenceNumber;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "job_type_id")
	private JobType jobType;

	@Column(name="job_summary")
	private String jobSummary;
	
	@Column(name="job_title")
	private String jobTitle;
	
	@Column(name="load_length")
	private int loadLength;
	
	@Column(name="load_height")
	private int loadHeight;
	
	@Column(name="load_width")
	private int loadWidth;
	
	@Column(name="load_weight")
	private int loadWeight;
	
	@Column(name="estimated_volume")
	private int estimatedVolume;
	
	@Column(name="package_space")
	private String packageSpace;
	
	@Column(name="load_description")
	private String loadDescription;
	
    @ManyToOne
	@JoinColumn(name = "job_category_id")
	private JobCategory jobCategory;

	@ManyToOne
	@JoinColumn(name = "load_category_id")
	private LoadCategory loadCategory;

	@ManyToOne
	@JoinColumn(name = "load_level_id")
	private LoadLevel loadLevel;
	
	@ManyToOne
	@JoinColumn(name = "vehicle_type_id")
	private VehicleType vehicleType;

	@OneToMany(mappedBy = "jobInfo")
	@JsonIgnore
	private Set<AssignJobInfo> assignJobInfo;

	@ManyToOne
	@JoinColumn(name = "truck_type_id")
	private TruckType truckType;
	
	@Column(name="pick_up_date_type")
	private String pickUpDateType;
	
	@Column(name="from_pick_up_date")
	private Date fromPickUpDate;
	
	@Column(name="to_pick_up_date")
	private Date toPickUpDate;
	
	@Column(name="fixed_pick_up_date")
	private Date fixedPickUpDate;
	
	@Column(name="pick_up_address",columnDefinition = "varchar(200) default ' '")
	private String pickUpAddress;

	@Column(name="truck_no",columnDefinition = "integer default 0")
	private int truckNo;

	@Column(name="pick_up_address_state")
	private String pickUpAddressState;

	@Column(name="pick_up_address_city")
	private String pickUpAddressCity;
	
	@Column(name="delivery_address",columnDefinition = "varchar(200) default ' '")
	private String deliveryAddress;

	@Column(name="delivery_address_state")
	private String deliveryAddressState;

	@Column(name="delivery_address_city")
	private String deliveryAddressCity;
	
	@Column(name="job_budget")
	private BigDecimal jobBudget;
	
	@Column(name="bid_deadline_date")
	private Date bidDeadLineDate;
    
	@Column(name="job_status",columnDefinition = "integer default 2")
	private int jobStatus;
	
	@Column(name="date_created")
	private Date dateCreated;
	
	@Column(name="is_removed")
	private boolean isRemoved;
	
	@ManyToOne
	@JoinColumn(name = "user_account_id")
	private UserAccount userAccount;
	
	@ManyToOne
	@JoinColumn(name = "job_id")
	private UserRatingLog  userRatingLog;
}
