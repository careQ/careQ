package com.reve.careQ.domain.RegisterChart.entity;

import com.reve.careQ.domain.Admin.entity.Admin;
import com.reve.careQ.domain.Member.entity.Member;
import com.reve.careQ.domain.Queue.entity.Queue;
import com.reve.careQ.global.compositePKEntity.CompositePKEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.LAZY;

@Getter
@RequiredArgsConstructor
@Entity
@SuperBuilder
public class RegisterChart {

    @EmbeddedId
    private CompositePKEntity id;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime time;

    @Column(nullable = false)
    private Integer status;

    @ManyToOne(fetch = LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "memberId")
    private Member member;

    @ManyToOne(fetch = LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "adminId")
    private Admin admin;

    @OneToOne(mappedBy = "registerChart", fetch = LAZY)
    private Queue queue;

}