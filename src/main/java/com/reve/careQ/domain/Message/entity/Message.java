package com.reve.careQ.domain.Message.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@RequiredArgsConstructor
@Entity
@SuperBuilder
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mg_id;

    @Column
    private String content;

    @Column
    private String sendTime;

}