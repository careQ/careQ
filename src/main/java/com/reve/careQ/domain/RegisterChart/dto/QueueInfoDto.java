package com.reve.careQ.domain.RegisterChart.dto;

import com.reve.careQ.domain.Admin.entity.Admin;
import com.reve.careQ.domain.Hospital.entity.Hospital;
import com.reve.careQ.domain.RegisterChart.entity.RegisterChart;
import com.reve.careQ.domain.Subject.entity.Subject;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueueInfoDto {
    private RegisterChart registerChart;
    private Subject subject;
    private Hospital hospital;
    private Admin admin;
    private Long waitingCount;
    private Long waitingTime;
    private String waitingStatus;

    public static QueueInfoDto of(RegisterChart registerChart, Subject subject, Hospital hospital, Admin admin, Long waitingCount, Long waitingTime, String waitingStatus) {
        return QueueInfoDto.builder()
                .registerChart(registerChart)
                .subject(subject)
                .hospital(hospital)
                .admin(admin)
                .waitingCount(waitingCount)
                .waitingTime(waitingTime)
                .waitingStatus(waitingStatus)
                .build();
    }
}
