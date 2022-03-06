package com.aikaload.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.util.Date;


@Data
@Entity(name="user_review")
@RequiredArgsConstructor
@NoArgsConstructor
public class UserReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @Column(name="revieweeId")
    @JsonIgnore
    @NonNull
    private Long revieweeId;

    @Column(name="reviewerId")
    @NonNull
    private Long reviewerId;

    @Column(name="comment")
    @NonNull
    private String comment;

    @Column(name="created_date")
    @NonNull
    private Date createdDate;

    @Column(name="modification_date")
    @JsonIgnore
    private Date modificationDate= new Date();
}
