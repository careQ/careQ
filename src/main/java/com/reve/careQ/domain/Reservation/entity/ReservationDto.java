package com.reve.careQ.domain.Reservation.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReservationDto {

    public enum UserType{
        MEMBER, ADMIN
    }

    private UserType userType;
    private Long memberId;
    private Long hospitalId;
    private Long subjectId;
    private ReservationStatus status;
}
