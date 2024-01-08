package com.reve.careQ.domain.RegisterChart.dto;

import com.reve.careQ.domain.RegisterChart.entity.RegisterChartStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterChartDto {

    private Long adminId;
    private Long memberId;
    private Long subjectId;
    private Long hospitalId;
    private String memberUsername;
    private LocalDateTime time;
    private RegisterChartStatus status;
}