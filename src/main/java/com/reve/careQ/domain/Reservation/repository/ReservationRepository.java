package com.reve.careQ.domain.Reservation.repository;

import com.reve.careQ.domain.Admin.entity.Admin;
import com.reve.careQ.domain.Member.entity.Member;
import com.reve.careQ.domain.Reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    boolean existsByDateAndAdminId(LocalDateTime date, Long adminId);

    List<Reservation> findByAdmin(Admin admin);

    Optional<Reservation> findByIdAdminIdAndIdMemberId(Long adminId, Long memberId);

    boolean existsByAdminIdAndMemberId(Long adminId, Long memberId);

    List<Reservation> findByMember(Member currentUser);

}