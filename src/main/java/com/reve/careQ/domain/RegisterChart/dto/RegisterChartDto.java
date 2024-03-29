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
    public enum UserType{
        MEMBER, ADMIN
    }
    private UserType userType;
    private Long adminId;
    private Long memberId;
    private Long subjectId;
    private String subjectName;
    private Long hospitalId;
    private String hospitalName;
    private String memberUsername;
    private LocalDateTime time;
    private RegisterChartStatus status;
}
