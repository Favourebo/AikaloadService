package com.aikaload.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity(name="load_level")
@Getter
@Setter
@NoArgsConstructor
public class LoadLevel {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonProperty("loadLevelId")
	private int id;

	@Column(name="name")
	@JsonProperty("loadLevelName")
	private String name;


	public LoadLevel(String name){
		this.name =  name;
	}

}
