package com.aikaload.entity;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity(name="notification")
@Getter
@Setter
@NoArgsConstructor
public class Notification {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "user_id")
	private UserAccount reportFor;
	
	@Column(name="description")
	private String descripion;
	
	@Column(name="notification_type")
	private int notificationType;
    
	@Column(name="activity_date")
    private Date notificationDate;
	
	@Column(name="is_read")
	private boolean isRead;
}
