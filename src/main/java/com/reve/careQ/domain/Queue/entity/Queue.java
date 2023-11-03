package com.reve.careQ.domain.Queue.entity;

import com.reve.careQ.domain.RegisterChart.entity.RegisterChart;
import com.reve.careQ.global.compositePKEntity.CompositePKEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@RequiredArgsConstructor
@Entity
@SuperBuilder
public class Queue {

    @EmbeddedId
    private CompositePKEntity id;

    @Column
    private Long waitingNum;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "memberId"),
            @JoinColumn(name = "adminId")
    })
    private RegisterChart registerChart;

}