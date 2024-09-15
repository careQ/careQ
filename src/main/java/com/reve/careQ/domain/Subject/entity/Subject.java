package com.reve.careQ.domain.Subject.entity;

import com.reve.careQ.global.baseEntity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@RequiredArgsConstructor
@Entity
@SuperBuilder
public class Subject extends BaseEntity {
    @Column(unique = true, nullable = false)
    private String code;

    @Column
    private String name;

}
