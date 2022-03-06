package com.aikaload.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import javax.persistence.*;
import java.util.Date;

@Data
@Entity(name="complaints")
public class Complaints {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @Column(name="reported_by")
    @JsonIgnore
    private Long reportedBy;

    @Column(name="user_reported")
    @JsonIgnore
    private Long userReported;

    @Column(name="treated")
    private boolean treated;

    @Column(name="message")
    private String message;

    @Column(name="created_date")
    private Date createdDate;

    @Column(name="modification_date")
    @JsonIgnore
    private Date modificationDate;
}
