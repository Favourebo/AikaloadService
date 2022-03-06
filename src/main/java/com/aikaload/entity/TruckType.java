package com.aikaload.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;
import java.util.Set;

@Entity(name="truck_type")
@Getter
@Setter
@NoArgsConstructor
public class TruckType {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonProperty("truckTypeId")
	private int id;

	@Column(name="truck_name")
	@JsonProperty("truckTypeName")
	private String name;

	@OneToMany(mappedBy = "truckType")
	@JsonIgnore
	private Set<TruckInfo> truckInfo;

	public TruckType(String name){
		this.name = name;
	}
}
