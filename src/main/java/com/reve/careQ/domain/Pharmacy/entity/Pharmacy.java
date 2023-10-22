package com.reve.careQ.domain.Pharmacy.entity;

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
public class Pharmacy extends BaseEntity {

    @Column
    private String name;

    @Column
    private String state;

    @Column
    private String city;

    @Column
    private Date days;

}