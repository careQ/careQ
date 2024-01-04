package com.reve.careQ.domain.Reservation.service;

import com.reve.careQ.domain.Admin.entity.Admin;
import com.reve.careQ.domain.RegisterChart.entity.RegisterChartStatus;
import com.reve.careQ.domain.Reservation.entity.Reservation;
import com.reve.careQ.domain.Reservation.entity.ReservationStatus;
import com.reve.careQ.global.rsData.RsData;

import java.util.List;
import java.util.Optional;

public interface ReservationService {
    Optional<Reservation> findByAdminIdAndMemberId(Long adminId, Long memberId);
    List<Reservation> getTodayReservation(Admin admin);
    String createReservationAndReturnRedirectUrl(Long hospitalId, Long subjectId, String selectedDate, String selectedTime);
    RsData<Reservation> insert(Long hospitalId, Long subjectId, String selectedDate, String selectedTime);
    RsData<String> checkDuplicateReservation(Long adminId, Long memberId);
    RsData<String> deleteReservation(Long reservationId);
    RsData<Reservation> updateStatus(Reservation reservation, ReservationStatus status);
    RsData<Reservation> updateRegisterStatus(Reservation reservation, RegisterChartStatus registerStatus);
    Reservation confirmReservation(Long adminId, Long memberId);
}
