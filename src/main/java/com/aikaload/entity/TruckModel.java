package com.aikaload.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;
import java.util.Set;

@Entity(name="truck_model")
@Getter
@Setter
@NoArgsConstructor
public class TruckModel {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonProperty("truckModelId")
	private int id;

	@Column(name="name")
	@JsonProperty("truckModelName")
	private String name;

	@OneToMany(mappedBy = "truckModel")
	@JsonIgnore
	private Set<TruckInfo> truckInfo;

	public TruckModel(String name){
		this.name = name;
	}
}
