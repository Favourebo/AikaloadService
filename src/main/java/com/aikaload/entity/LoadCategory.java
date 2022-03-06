package com.aikaload.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
@Entity(name="load_category")
@Getter
@Setter
@NoArgsConstructor
public class LoadCategory {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonProperty("materialTypeId")
	private Long id;

	@JsonProperty("materialTypeName")
	private String name;

	public LoadCategory(String name){
		this.name = name;
	}
}
