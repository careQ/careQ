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

    @OneToMany(mappedBy = "hospital", fetch = LAZY)
    private List<Admin> AdminList;

}