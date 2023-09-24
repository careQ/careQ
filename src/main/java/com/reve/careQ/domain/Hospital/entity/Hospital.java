package com.reve.careQ.domain.Hospital.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Getter
@RequiredArgsConstructor
@Entity
@SuperBuilder
public class Hospital {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long hospital_id;

    @Column
    private String hospital_name;

    @Column
    private String hospital_state;

    @Column
    private String hospital_city;

    @Column
    private String hospital_type;

    @Column
    private String hospital_subject;

    @Column
    private Date hospital_days;

}