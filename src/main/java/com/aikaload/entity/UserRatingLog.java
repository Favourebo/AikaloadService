package com.aikaload.entity;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity(name="user_rating_log")
@Getter
@Setter
@NoArgsConstructor
public class UserRatingLog {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * This contains truck_owner_id
	 */
	@ManyToOne
	@JoinColumn(name = "user_account_id")
	private UserAccount userAccount;

	@Column(name="is_favourite")
	private boolean isFavourite;

	@Column(name="deal_amount")
	private BigDecimal dealAmount;

	@Column(name="user_rating")
	private int userRating;

	@Column(name="comment")
	private String comment;
	
	@ManyToOne
	@JoinColumn(name = "job_id")
	private JobInfo jobInfo;
	
	@Column(name="date_rated")
	private Date dateRated;
	
	@Column(name="date_created")
	private Date dateCreated;
}
