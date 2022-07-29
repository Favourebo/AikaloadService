package com.aikaload.entity;

import com.aikaload.enums.TransmissionEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import javax.persistence.*;
import java.util.Date;
import java.util.Set;


@Data
@Entity
@Table(name="truck_info")
public class TruckInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "truck_model_id")
    private TruckModel truckModel;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "truck_type_id")
    private TruckType truckType;

    @Column(name="destination_state")
    private String truckYear;

    @Column(name="created_date")
    private Date createdDate;

    @Column(name="last_modified_date")
    private Date lastModifiedDate;

    @Column(name="destination_city")
    private String plateNumber;

    @OneToMany(mappedBy = "truckInfo")
    @JsonIgnore
    private Set<TruckImages> truckImages;

    @ManyToOne
    @JoinColumn(name = "user_account_id")
    private UserAccount userAccount;


    @Column(name="truck_status",columnDefinition = "integer default 1")
    private int truckStatus;

    @Column(name="is_verified")
    private boolean isVerified;

    @Column(name="description")
    private String description;

    @Column(name="truck_name")
    private String truckName;

    @Column(name="location")
    private String location;

    @Column(name="truck_size")
    private String truckSize;

    @Column(name="transmission_type")
    private String transmission;


    @Column(name="is_airconditioner_available")
    private boolean isAirConditionerAvailable;

    private String longitude;

    private String latitude;

    private String address;

}
