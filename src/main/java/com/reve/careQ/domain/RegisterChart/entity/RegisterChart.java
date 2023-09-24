package com.reve.careQ.domain.RegisterChart.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
@Entity
@SuperBuilder
public class RegisterChart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long registerChart_id;

    @Column
    @CreationTimestamp
    private LocalDateTime register_time;

}
