package com.reve.careQ.domain.RegisterChart.entity;

import com.reve.careQ.domain.Admin.entity.Admin;
import com.reve.careQ.domain.Hospital.entity.Hospital;
import com.reve.careQ.domain.Subject.entity.Subject;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterChartInfoDto {
    private RegisterChart registerChart;
    private Subject subject;
    private Hospital hospital;
    private Admin admin;

    public static RegisterChartInfoDto of(RegisterChart registerChart, Subject subject, Hospital hospital, Admin admin) {
        return RegisterChartInfoDto.builder()
                .registerChart(registerChart)
                .subject(subject)
                .hospital(hospital)
                .admin(admin)
                .build();
    }
}
