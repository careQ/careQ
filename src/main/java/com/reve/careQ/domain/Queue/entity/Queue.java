package com.reve.careQ.domain.Queue.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@RequiredArgsConstructor
@Entity
@SuperBuilder
public class Queue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long queue_id;

    @Column
    private Long waiting_num;

}
