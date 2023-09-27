package com.reve.careQ.domain.Subject.entity;

import com.reve.careQ.domain.Admin.entity.Admin;
import com.reve.careQ.global.baseEntity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@Getter
@RequiredArgsConstructor
@Entity
@SuperBuilder
public class Subject extends BaseEntity {

    @Column
    private String sub_name;

    @OneToMany(mappedBy = "subject", fetch = LAZY)
    private List<Admin> adminList;

}
