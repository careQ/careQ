package com.reve.careQ.domain.Reservation.entity;

import com.reve.careQ.domain.Admin.entity.Admin;
import com.reve.careQ.domain.Member.entity.Member;
import com.reve.careQ.domain.RegisterChart.dto.RegisterChartDto;
import com.reve.careQ.domain.RegisterChart.entity.RegisterChartStatus;
import com.reve.careQ.global.baseEntity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Getter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private Admin admin;

    @Column(nullable = false)
    private LocalDateTime date;

    @ColumnDefault("FALSE")
    @Column(nullable = false)
    private Boolean isDeleted;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RegisterChartStatus registerStatus;

    public void setMember(Member member) {
        this.member = member;
    }
    public void setAdmin(Admin admin) {
        this.admin = admin;
    }
    public void markAsDeleted(Boolean b) {
        this.isDeleted = b;
    }
    public void setStatus(ReservationStatus status) {
        this.status = status;
    }
    public void setRegisterStatus(RegisterChartStatus registerStatus) {
        this.registerStatus = registerStatus;
    }
    public String getFormattedDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return date.format(formatter);
    }

    public RegisterChartDto toResponse() {
        return RegisterChartDto.builder()
                .adminId(this.admin.getId())
                .memberId(this.member.getId())
                .subjectId(this.admin.getSubject().getId())
                .subjectName(this.admin.getSubject().getName())
                .hospitalId(this.admin.getHospital().getId())
                .hospitalName(this.admin.getHospital().getName())
                .memberUsername(this.member.getUsername())
                .time(this.getModifyDate())
                .status(this.registerStatus)
                .build();
    }
}
