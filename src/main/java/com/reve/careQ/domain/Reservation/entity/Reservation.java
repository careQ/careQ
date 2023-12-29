package com.reve.careQ.domain.Reservation.entity;

import com.reve.careQ.domain.Admin.entity.Admin;
import com.reve.careQ.domain.Member.entity.Member;
import com.reve.careQ.domain.RegisterChart.entity.RegisterChartStatus;
import com.reve.careQ.global.baseEntity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
@Entity
@SuperBuilder
public class Reservation extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private Admin admin;

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RegisterChartStatus registerStatus;

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }
    public void setRegisterStatus(RegisterChartStatus registerStatus) {
        this.registerStatus = registerStatus;
    }
}
