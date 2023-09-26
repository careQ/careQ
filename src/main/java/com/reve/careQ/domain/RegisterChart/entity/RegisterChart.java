package com.reve.careQ.domain.RegisterChart.entity;

import com.reve.careQ.domain.HospSub.entity.HospSub;
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
    private CompositePKEntity registerChart_id;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime register_time;

    @Column(nullable = false)
    private Integer status;

    @ManyToOne(fetch = LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "hospsub_id")
    private HospSub hospSub;
    
    @OneToOne(mappedBy = "registerChart", fetch = LAZY)
    private Queue queue;

}
