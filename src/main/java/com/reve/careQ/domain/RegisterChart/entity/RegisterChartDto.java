package com.reve.careQ.domain.RegisterChart.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class RegisterChartDto {
    private Long adminId;

    private Long memberId;

    private Long subjectId;

    private Long hospitalId;

    private String memberUsername;

    private LocalDateTime time;

    private RegisterChartStatus status;

    public RegisterChartDto(Long adminId, Long memberId, Long subjectId, Long hospitalId, String memberUsername, LocalDateTime time, RegisterChartStatus status){
        this.adminId = adminId;
        this.memberId = memberId;
        this.subjectId = subjectId;
        this.hospitalId = hospitalId;
        this.memberUsername = memberUsername;
        this.time = time;
        this.status = status;
    }

}
