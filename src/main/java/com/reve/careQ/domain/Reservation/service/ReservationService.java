package com.reve.careQ.domain.Reservation.service;

import com.reve.careQ.domain.Admin.entity.Admin;
import com.reve.careQ.domain.RegisterChart.dto.RegisterChartDto;
import com.reve.careQ.domain.RegisterChart.entity.RegisterChartStatus;
import com.reve.careQ.domain.Reservation.entity.Reservation;
import com.reve.careQ.domain.Reservation.entity.ReservationStatus;
import com.reve.careQ.global.rsData.RsData;

import java.util.List;
import java.util.Optional;

public interface ReservationService {
    Reservation findById(Long id);
    List<Reservation> findByMemberIdAndIsDeletedFalse(Long memberId);
    Optional<Reservation> findByAdminIdAndMemberId(Long adminId, Long memberId);
    List<Reservation> getTodayReservation(Admin admin);
    Reservation createReservation(Long hospitalId, Long subjectId, String selectedDate, String selectedTime);
    String createReservationWithCheckAndReturnRedirectUrl(Long hospitalId, Long subjectId, String selectedDate, String selectedTime);
    RsData<Reservation> insert(Long hospitalId, Long subjectId, String selectedDate, String selectedTime);
    RsData<String> checkDuplicateReservation(Long adminId, Long memberId);
    RsData<String> deleteReservation(Long reservationId);
    RsData<Reservation> updateStatus(Reservation reservation, ReservationStatus status);
    RsData<Reservation> updateStatusByAdminAndMember(Admin admin, Long memberId, RegisterChartStatus status);
    Optional<Reservation> findReservationByAdminIdAndMemberIdAndIsDeletedFalse(Long adminId, Long memberId);
    List<RegisterChartDto> getReservationsByMemberIdAndRegisterStatus(Long memberId);
}
