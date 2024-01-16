package com.reve.careQ.domain.Reservation.repository;

import com.reve.careQ.domain.Admin.entity.Admin;
import com.reve.careQ.domain.Member.entity.Member;
import com.reve.careQ.domain.RegisterChart.entity.RegisterChartStatus;
import com.reve.careQ.domain.Reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    boolean existsByDateAndAdminIdAndIsDeletedFalse(LocalDateTime date, Long adminId);
    List<Reservation> findByAdmin(Admin admin);
    List<Reservation> findByMemberIdAndIsDeletedFalse(Long memberId);
    boolean existsByAdminIdAndMemberIdAndIsDeletedFalse(Long adminId, Long memberId);
    List<Reservation> findByMember(Member member);
    @Query("SELECT reservation FROM Reservation reservation WHERE reservation.admin = :admin " +
            "AND FUNCTION('DATE_FORMAT', reservation.date, '%Y-%m-%d') = CURRENT_DATE")
    List<Reservation> getTodayReservation(@Param("admin") Admin admin);
    Optional<Reservation> findByAdminIdAndMemberIdAndIsDeletedFalse(Long adminId, Long memberId);
    Optional<Reservation> findReservationByAdminIdAndMemberIdAndIsDeletedFalse(Long adminId, Long memberId);
    List<Reservation> findByMemberIdAndRegisterStatus(Long memberId, RegisterChartStatus status);
}

