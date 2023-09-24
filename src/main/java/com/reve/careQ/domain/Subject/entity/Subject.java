package com.reve.careQ.domain.Subject.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@RequiredArgsConstructor
@Entity
@SuperBuilder
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sub_id;

    @Column(unique = true)
    private String sub_name;

}
