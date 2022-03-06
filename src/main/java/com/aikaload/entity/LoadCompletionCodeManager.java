package com.aikaload.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;
import java.util.Date;

@Entity(name="load_completion_code_manager")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoadCompletionCodeManager {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name="assign_job_id")
	private Long assignJobId;

	@Column(name="completion_code")
	private String completionCode;

	@Column(name="load_code")
	private String loadCode;

	@Column(name="created_Date")
	private Date createdDate;

	@Column(name="truck_id")
	private int truckId;

	@Column(name="is_used")
	private boolean isUsed;

	@Column(name="status", columnDefinition = "varchar(50) default 'FRESH'")
	private String status;

	@Column(name="admin_status", columnDefinition = "varchar(50) default 'PENDING'")
	private String adminStatus;


}
