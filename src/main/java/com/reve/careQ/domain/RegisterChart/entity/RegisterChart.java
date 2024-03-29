package com.reve.careQ.domain.RegisterChart.entity;

import com.reve.careQ.domain.Admin.entity.Admin;
import com.reve.careQ.domain.Member.entity.Member;
import com.reve.careQ.domain.RegisterChart.dto.RegisterChartDto;
import com.reve.careQ.global.baseEntity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RegisterChart extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private Admin admin;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime time;

    @ColumnDefault("FALSE")
    @Column(nullable = false)
    private Boolean isDeleted;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RegisterChartStatus status;

    public RegisterChartDto toResponse() {
        return RegisterChartDto.builder()
                .adminId(this.admin.getId())
                .memberId(this.member.getId())
                .subjectId(this.admin.getSubject().getId())
                .subjectName(this.admin.getSubject().getName())
                .hospitalId(this.admin.getHospital().getId())
                .hospitalName(this.admin.getHospital().getName())
                .memberUsername(this.member.getUsername())
                .time(this.time)
                .status(this.status)
                .build();
    }

    public void setMember(Member member) {
        this.member = member;
    }
    public void setAdmin(Admin admin) {
        this.admin = admin;
    }
    public void markAsDeleted(Boolean b) {
        this.isDeleted = b;
    }
    public void setStatus(RegisterChartStatus status) {
        this.status = status;
    }
}