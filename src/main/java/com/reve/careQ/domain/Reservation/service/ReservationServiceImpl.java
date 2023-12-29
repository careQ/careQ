package com.reve.careQ.domain.Reservation.service;

import com.reve.careQ.domain.Admin.entity.Admin;
import com.reve.careQ.domain.Admin.service.AdminService;

import com.reve.careQ.domain.Member.entity.Member;
import com.reve.careQ.domain.Member.service.MemberService;
import com.reve.careQ.domain.RegisterChart.entity.RegisterChartStatus;
import com.reve.careQ.domain.Reservation.entity.Reservation;

import com.reve.careQ.domain.Reservation.entity.ReservationStatus;
import com.reve.careQ.domain.Reservation.repository.ReservationRepository;
import com.reve.careQ.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final AdminService adminService;
    private final MemberService memberService;

    private Member getCurrentUser() {
        return memberService.getCurrentUser().orElseThrow(() -> new RuntimeException("현재 로그인한 사용자 정보를 가져오지 못했습니다."));
    }

    @Transactional(readOnly = true)
    public Optional<Reservation> findByAdminIdAndMemberId(Long adminId, Long memberId){
        return reservationRepository.findByAdminIdAndMemberIdAndIsDeletedFalse(adminId, memberId);
    }

    @Transactional(readOnly = true)
    public List<Reservation> getTodayReservation(Admin admin){
        return reservationRepository.getTodayReservation(admin);
    }

    @Transactional
    public Reservation createReservation(Long hospitalId, Long subjectId, String selectedDate, String selectedTime) {
        Member currentUser = getCurrentUser();
        Admin admin = adminService.findByHospitalIdAndSubjectId(hospitalId, subjectId)
                .orElseThrow(() -> new RuntimeException("해당 병원과 진료 과목에 해당하는 관리자를 찾을 수 없습니다."));
        LocalDateTime dateTime = LocalDateTime.parse(selectedDate + "T" + selectedTime);

        checkDuplicateReservation(dateTime, admin.getId());

        return saveReservation(dateTime, admin, currentUser);
    }

    @Transactional
    public RsData<Reservation> insert(Long hospitalId, Long subjectId, String selectedDate, String selectedTime) {
        Reservation reservation = createReservation(hospitalId, subjectId, selectedDate, selectedTime);
        return RsData.of("S-1", "예약이 성공적으로 등록되었습니다.", reservation);
    }

    public String createReservationAndReturnRedirectUrl(Long hospitalId, Long subjectId, String selectedDate, String selectedTime) {
        Reservation reservation = createReservation(hospitalId, subjectId, selectedDate, selectedTime);
        return generateRedirectUrl(subjectId, hospitalId, reservation.getId());
    }

    @Transactional(readOnly = true)
    public RsData<String> checkDuplicateReservation(Long adminId, Long memberId) {
        boolean isDuplicateActiveReservation = reservationRepository.existsByAdminIdAndMemberIdAndIsDeletedFalse(adminId, memberId);

        if (isDuplicateActiveReservation) {
            return RsData.of("F-4", "이미 예약한 병원입니다.");
        }

        return RsData.of("S-3", "예약 가능한 병원입니다.");
    }

    private void checkDuplicateReservation(LocalDateTime dateTime, Long adminId) {
        if (reservationRepository.existsByDateAndAdminId(dateTime, adminId)) {
            throw new RuntimeException("이미 예약된 시간대입니다.");
        }
    }

    @Transactional
    public RsData<String> deleteReservation(Long reservationId) {
        Optional<Reservation> optionalReservation = reservationRepository.findById(reservationId);

        if (optionalReservation.isPresent()) {
            Reservation reservation = optionalReservation.get();
            reservation.markAsDeleted(true);
            reservationRepository.save(reservation);
            return RsData.of("S-2", "예약 정보가 삭제되었습니다.");
        } else {
            return RsData.of("F-5", "예약 정보를 찾을 수 없습니다.");
        }
    }

    @Transactional
    public RsData<Reservation> updateStatus(Reservation reservation, ReservationStatus status){
        reservation.setStatus(status);
        return saveAndReturnRsData(reservation, "예약 상태가 업데이트 되었습니다.");
    }

    @Transactional
    public RsData<Reservation> updateRegisterStatus(Reservation reservation, RegisterChartStatus registerStatus){
        reservation.setRegisterStatus(registerStatus);
        return saveAndReturnRsData(reservation, "진료 상태가 업데이트 되었습니다.");
    }

    private String generateRedirectUrl(Long subjectId, Long hospitalId, Long reservationId) {
        return "/members/subjects/" + subjectId + "/hospitals/" + hospitalId + "/reservations/" + reservationId;
    }

    private Reservation saveReservation(LocalDateTime dateTime, Admin admin, Member member) {
        Reservation reservation = Reservation.builder()
                .date(dateTime)
                .status(ReservationStatus.PENDING)
                .registerStatus(RegisterChartStatus.WAITING)
                .admin(admin)
                .member(member)
                .isDeleted(false)
                .build();
        return reservationRepository.save(reservation);
    }

    private RsData<Reservation> saveAndReturnRsData(Reservation reservation, String msg){
        reservationRepository.save(reservation);
        return RsData.of("S-1", msg, reservation);
    }
}
