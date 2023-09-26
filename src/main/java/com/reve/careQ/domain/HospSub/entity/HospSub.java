package com.reve.careQ.domain.HospSub.entity;

import com.reve.careQ.domain.Admin.entity.Admin;
import com.reve.careQ.domain.Hospital.entity.Hospital;
import com.reve.careQ.domain.RegisterChart.entity.RegisterChart;
import com.reve.careQ.domain.Reservation.entity.Reservation;
import com.reve.careQ.domain.Subject.entity.Subject;
import com.reve.careQ.global.baseEntity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@Getter
@RequiredArgsConstructor
@Entity
@SuperBuilder
public class HospSub extends BaseEntity {

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name="hospital_id")
    private Hospital hospital;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name="subject_id")
    private Subject subject;

    @OneToMany(mappedBy = "hospSub", fetch = LAZY)
    private List<RegisterChart> registerChartList;

    @OneToMany(mappedBy = "hospSub", fetch = LAZY)
    private List<Reservation> reservationList;

    @OneToMany(mappedBy = "hospSub", fetch = LAZY)
    private List<Admin> adminList;

}
