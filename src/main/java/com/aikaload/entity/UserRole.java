package com.aikaload.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="user_role")
@Getter
@Setter
@NoArgsConstructor
public class UserRole {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name="user_role_name")
	private String userRoleName;
	
	@Column(name="user_role_description")
	private String userRoleDescription;

	@OneToMany(mappedBy = "userRole")
	@JsonIgnore
	private Set<UserAccount> userAccount;

	public  UserRole(String userRoleName, String userRoleDescription){
		this.userRoleName = userRoleName;
		this.userRoleDescription = userRoleDescription;
	}
}
