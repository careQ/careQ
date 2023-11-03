package com.reve.careQ.domain.Reservation.entity;

import com.reve.careQ.domain.Admin.entity.Admin;
import com.reve.careQ.domain.Member.entity.Member;
import com.reve.careQ.global.compositePKEntity.CompositePKEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.LAZY;

@Getter
//@Setter
@RequiredArgsConstructor
@Entity
@SuperBuilder
public class Reservation {

    @CreatedDate
    private LocalDateTime createDate;

    @LastModifiedDate
    private LocalDateTime modifyDate;

    private LocalDateTime deleteDate;

    @EmbeddedId
    private CompositePKEntity id;

    @MapsId("memberId")
    @JoinColumn(name = "member_id")
    @ManyToOne(fetch = LAZY, cascade = CascadeType.PERSIST)
    private Member member;

    @MapsId("adminId")
    @JoinColumn(name = "admin_id")
    @ManyToOne(fetch = LAZY, cascade = CascadeType.PERSIST)
    private Admin admin;

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

}