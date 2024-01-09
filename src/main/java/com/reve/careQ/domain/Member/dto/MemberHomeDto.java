package com.reve.careQ.domain.Member.dto;

import com.reve.careQ.domain.RegisterChart.entity.RegisterChartStatus;
import com.reve.careQ.domain.Reservation.entity.Reservation;
import lombok.Data;

import java.util.List;

@Data
public class MemberHomeDto {
    private List<Reservation> reservations;
    private Long waitingCount;
    private RegisterChartStatus currentStatus;

    public MemberHomeDto(List<Reservation> reservations, Long waitingCount, RegisterChartStatus currentStatus) {
        this.reservations = reservations;
        this.waitingCount = waitingCount;
        this.currentStatus = currentStatus;
    }
}
