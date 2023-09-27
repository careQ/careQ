package com.reve.careQ.domain.Hospital.entity;

import com.reve.careQ.domain.Admin.entity.Admin;
import com.reve.careQ.global.baseEntity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@Getter
@RequiredArgsConstructor
@Entity
@SuperBuilder
public class Hospital extends BaseEntity {

    @Column
    private String hospital_name;

    @Column
    private String hospital_state;

    @Column
    private String hospital_city;

    @Column
    private String hospital_type;

    @Column
    private String hospital_subjectlist;

    @Column
    private Date hospital_days;

    @OneToMany(mappedBy = "hospital", fetch = LAZY)
    private List<Admin> AdminList;

}