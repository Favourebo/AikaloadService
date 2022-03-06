package com.aikaload.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity(name="vehicle_type")
@Getter
@Setter
@NoArgsConstructor
public class VehicleType {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonProperty("vehicleTypeId")
	private Long id;

	@JsonProperty("vehicleTypeName")
	private String name;

	@OneToMany(mappedBy = "vehicleType")
	@JsonIgnore
	private Set<JobInfo> jobInfo;
}
