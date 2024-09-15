package com.reve.careQ.domain.Hospital.entity;

import com.reve.careQ.global.baseEntity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Getter
@RequiredArgsConstructor
@Entity
@SuperBuilder
public class Hospital extends BaseEntity {
    @Column(unique = true, nullable = false)
    private String code;

    @Column
    private String name;

    @Column
    private String state;

    @Column
    private String city;

    @Column
    private String type;

    @Column
    private String hospitalSubjectList;

    @Column
    private Date days;

}