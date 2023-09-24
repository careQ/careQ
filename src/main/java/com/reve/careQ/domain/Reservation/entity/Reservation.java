package com.reve.careQ.domain.Reservation.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
@Entity
@SuperBuilder
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservation_id;

    @Column(nullable = false)
    private LocalDateTime reservation_date;

    @Column
    private Enum reservation_status;

}
