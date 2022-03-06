package com.aikaload.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity(name="assign_job_info")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AssignJobInfo {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "job_info_id")
	private JobInfo jobInfo;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "assigned_to")
	private UserAccount assignedTo;

	@Column(name="is_assigned")
	private boolean isAssigned;

	@Column(name="is_tasked_closed")
	private boolean isTaskClosed;

	@Column(name = "created_date")
	private Date createdDate;

	@Column(name = "assigned_date")
	private Date assignedDate;

	@Column(name = "offer_amount" , columnDefinition="Decimal(10,2) default '0.00'")
	private BigDecimal offerAmount;

	@Column(name = "available_truck_number", columnDefinition = "integer default 0")
	private int truckNumber;

	@Column(name="assigned_truck_number", columnDefinition = "integer default 0")
	private int assignedTruckNumber;

	@Column(name = "token", columnDefinition = "varchar(100) default 'NIL'")
	private String token;

	@Column(name = "expected_delivery_days", columnDefinition = "integer default 1")
	private int expectedDeliveryDays;

	@Column(name = "closed_task_date")
	private Date closedTaskDate;
}
