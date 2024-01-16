package com.reve.careQ.domain.Reservation.service;

import com.reve.careQ.domain.Admin.entity.Admin;

import com.reve.careQ.domain.Admin.service.AdminService;
import com.reve.careQ.domain.Member.entity.Member;
import com.reve.careQ.domain.Member.service.MemberService;
import com.reve.careQ.domain.RegisterChart.dto.RegisterChartDto;
import com.reve.careQ.domain.RegisterChart.entity.RegisterChartStatus;
import com.reve.careQ.domain.Reservation.entity.Reservation;

import com.reve.careQ.domain.Reservation.entity.ReservationStatus;
import com.reve.careQ.domain.Reservation.exception.ReservationNotFoundException;
import com.reve.careQ.domain.Reservation.repository.ReservationRepository;
import com.reve.careQ.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final AdminService adminService;
    private final MemberService memberService;

    private Member getCurrentUser() {
        return memberService.getCurrentUser().orElseThrow(() -> new RuntimeException("현재 로그인한 사용자 정보를 가져오지 못했습니다."));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reservation> findByMemberIdAndIsDeletedFalse(Long memberId) {
        return reservationRepository.findByMemberIdAndIsDeletedFalse(memberId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Reservation> findByAdminIdAndMemberId(Long adminId, Long memberId){
        return reservationRepository.findByAdminIdAndMemberIdAndIsDeletedFalse(adminId, memberId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reservation> getTodayReservation(Admin admin){
        return reservationRepository.getTodayReservation(admin);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Reservation> findReservationByAdminIdAndMemberIdAndIsDeletedFalse(Long adminId, Long memberId){
        return reservationRepository.findReservationByAdminIdAndMemberIdAndIsDeletedFalse(adminId, memberId);
    }

    public List<Reservation> getReservationsForMember(Member member) {
        return reservationRepository.findByMember(member);
    }

    @Override
    @Transactional
    public Reservation createReservation(Long hospitalId, Long subjectId, String selectedDate, String selectedTime) {
        Member currentUser = getCurrentUser();
        Admin admin = getAdmin(hospitalId, subjectId);
        LocalDateTime dateTime = parseDateTime(selectedDate, selectedTime);

        validateReservation(dateTime, admin.getId());

        return saveReservation(dateTime, admin, currentUser);
    }

    private Admin getAdmin(Long hospitalId, Long subjectId) {
        return adminService.findByHospitalIdAndSubjectId(hospitalId, subjectId)
                .orElseThrow(() -> new RuntimeException("해당 병원과 진료 과목에 해당하는 관리자를 찾을 수 없습니다."));
    }

    private LocalDateTime parseDateTime(String selectedDate, String selectedTime) {
        return LocalDateTime.parse(selectedDate + "T" + selectedTime);
    }

    private void validateReservation(LocalDateTime dateTime, Long adminId) {
        RsData<String> checkResult = checkDuplicateTime(dateTime, adminId);
        if (!checkResult.isSuccess()) {
            throw new RuntimeException(checkResult.getMsg());
        }
    }

    private RsData<String> checkDuplicateTime(LocalDateTime dateTime, Long adminId) {
        boolean isTimeBooked = reservationRepository.existsByDateAndAdminIdAndIsDeletedFalse(dateTime, adminId);
        if (isTimeBooked) {
            return RsData.of("F-5", "이미 예약된 시간대입니다.");
        }
        return RsData.of("S-3", "예약 가능한 시간대입니다.");
    }

    @Override
    @Transactional
    public RsData<Reservation> insert(Long hospitalId, Long subjectId, String selectedDate, String selectedTime) {
        Reservation reservation = createReservation(hospitalId, subjectId, selectedDate, selectedTime);
        return RsData.of("S-1", "예약이 성공적으로 등록되었습니다.", reservation);
    }

    @Override
    @Transactional
    public String createReservationWithCheckAndReturnRedirectUrl(Long hospitalId, Long subjectId, String selectedDate, String selectedTime) {
        Member currentUser = getCurrentUser();
        Admin admin = getAdmin(hospitalId, subjectId);
        LocalDateTime dateTime = parseDateTime(selectedDate, selectedTime);

        checkReservationExists(admin.getId(), currentUser.getId());
        checkTimeIsBooked(dateTime, admin.getId());

        Reservation reservation = createReservation(hospitalId, subjectId, selectedDate, selectedTime);
        return generateRedirectUrl(subjectId, hospitalId, reservation.getId());
    }

    private void checkReservationExists(Long adminId, Long memberId) {
        RsData<String> reservationStatus = checkDuplicateReservation(adminId, memberId);
        if (!reservationStatus.isSuccess()) {
            throw new ReservationNotFoundException(reservationStatus.getMsg());
        }
    }

    private void checkTimeIsBooked(LocalDateTime dateTime, Long adminId) {
        RsData<String> duplicateTimeStatus = checkDuplicateTime(dateTime, adminId);
        if (!duplicateTimeStatus.isSuccess()) {
            throw new ReservationNotFoundException(duplicateTimeStatus.getMsg());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public RsData<String> checkDuplicateReservation(Long adminId, Long memberId) {
        boolean isDuplicateActiveReservation = reservationRepository.existsByAdminIdAndMemberIdAndIsDeletedFalse(adminId, memberId);
        if (isDuplicateActiveReservation) {
            return RsData.of("F-4", "이미 예약한 병원입니다.");
        }
        return RsData.of("S-3", "예약 가능한 병원입니다.");
    }

    @Override
    @Transactional
    public RsData<String> deleteReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("예약 정보를 찾을 수 없습니다."));

        reservation.markAsDeleted(true);
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservation.setRegisterStatus(RegisterChartStatus.CANCELLED);
        reservationRepository.save(reservation);
        return RsData.of("S-2", "예약 정보가 삭제되었습니다.");
    }

    @Override
    @Transactional
    public RsData<Reservation> updateStatus(Reservation reservation, ReservationStatus status){
        reservation.setStatus(status);
        return saveAndReturnRsData(reservation, "예약 상태가 업데이트 되었습니다.");
    }

    @Override
    @Transactional
    public RsData<Reservation> updateStatusByAdminAndMember(Admin admin, Long memberId, RegisterChartStatus status) {
        Reservation reservation = reservationRepository.findReservationByAdminIdAndMemberIdAndIsDeletedFalse(admin.getId(), memberId)
                .orElseThrow(() -> new IllegalArgumentException("예약 정보를 찾을 수 없습니다."));

        if(status.equals(RegisterChartStatus.ENTER)){
            reservation.setRegisterStatus(RegisterChartStatus.ENTER);
        } else if(status.equals(RegisterChartStatus.COMPLETE)){
            reservation.setRegisterStatus(RegisterChartStatus.COMPLETE);
            reservation.markAsDeleted(true);
        } else if(status.equals(RegisterChartStatus.CANCELLED)){
            reservation.setRegisterStatus(RegisterChartStatus.CANCELLED);
            reservation.markAsDeleted(true);
        }

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

    @Transactional(readOnly = true)
    public List<RegisterChartDto> getReservationsByMemberIdAndRegisterStatus(Long memberId){
        List<Reservation> reservations = reservationRepository.findByMemberIdAndRegisterStatus(memberId, RegisterChartStatus.COMPLETE);
        return reservations.stream().map(Reservation::toResponse).collect(Collectors.toList());
    }
}