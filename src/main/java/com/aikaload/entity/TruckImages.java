package com.aikaload.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name="truck_images")
public class TruckImages {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "truck_info_id")
    private TruckInfo truckInfo;

    private String truckImageUrl;
}
