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
    private String pharmacy_name;

    @Column
    private String pharmacy_state;

    @Column
    private String pharmacy_city;

    @Column
    private Date pharmacy_days;

}
