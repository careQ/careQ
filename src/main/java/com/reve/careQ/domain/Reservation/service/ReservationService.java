package com.reve.careQ.domain.Reservation.service;

import com.reve.careQ.domain.Admin.entity.Admin;
import com.reve.careQ.domain.Admin.service.AdminService;

import com.reve.careQ.domain.Member.entity.Member;
import com.reve.careQ.domain.Member.service.MemberService;
import com.reve.careQ.domain.RegisterChart.entity.RegisterChartStatus;
import com.reve.careQ.domain.Reservation.entity.Reservation;

import com.reve.careQ.domain.Reservation.entity.ReservationStatus;
import com.reve.careQ.domain.Reservation.repository.ReservationRepository;
import com.reve.careQ.global.compositePKEntity.CompositePKEntity;
import com.reve.careQ.global.rsData.RsData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service

@Transactional(readOnly = true)
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final AdminService adminService;
    private final MemberService memberService;

    @Autowired
    public ReservationService(ReservationRepository reservationRepository, AdminService adminService, MemberService memberService) {
        this.reservationRepository = reservationRepository;
        this.adminService = adminService;
        this.memberService = memberService;
    }

    public Optional<Reservation> findByIdAdminIdAndIdMemberId(Long adminId, Long memberId){
        return reservationRepository.findByIdAdminIdAndIdMemberId(adminId, memberId);
    }

    public boolean existsByAdminIdAndMemberId(Long adminId, Long memberId){
        return reservationRepository.existsByAdminIdAndMemberId(adminId, memberId);
    }

    public List<Reservation> getTodayReservation(Admin admin){
        return reservationRepository.getTodayReservation(admin);
    }

    @Transactional
    public RsData<Reservation> insert(Long hospitalId, Long subjectId, String selectedDate, String selectedTime) {
        // 사용자 정보 가져오기
        RsData<Member> currentUserData = memberService.getCurrentUser();

        if (currentUserData.isSuccess()) {
            Member currentUser = currentUserData.getData();
            Optional<Admin> adminOptional = adminService.findByHospitalIdAndSubjectId(hospitalId, subjectId);

            if (adminOptional.isEmpty()) {
                return RsData.of("F-2", "해당 병원과 진료 과목에 해당하는 관리자를 찾을 수 없습니다.");
            }

            LocalDateTime dateTime = LocalDateTime.parse(selectedDate + "T" + selectedTime);

            // 중복 예약을 확인
            boolean isDuplicateReservation = reservationRepository.existsByDateAndAdminId(dateTime, adminOptional.get().getId());

            if (isDuplicateReservation) {
                return RsData.of("F-4", "이미 예약된 시간대입니다.");
            }

            // 가져온 Admin 및 Member 엔티티 설정
            Admin admin = adminOptional.get();
            Member member = currentUser;

            // UUID 생성
            UUID reservationId = UUID.randomUUID();

            // 복합 키 생성
            CompositePKEntity id = new CompositePKEntity();
            id.setAdminId(admin.getId());
            id.setMemberId(member.getId());


            Reservation reservation = Reservation.builder()
                    .id(id)
                    .date(dateTime)
                    .status(ReservationStatus.PENDING)
                    .registerStatus(RegisterChartStatus.WAITING)
                    .admin(admin)
                    .member(member)
                    .build();

            Reservation savedReservation = reservationRepository.save(reservation);

            return RsData.of("S-1", "예약 테이블에 삽입되었습니다.", savedReservation);
        } else {
            return RsData.of("F-3", "현재 로그인한 사용자 정보를 가져오지 못했습니다.");
        }

    }

    // 예약 정보 삭제
    @Transactional
    public RsData<String> deleteReservation(CompositePKEntity id) {
        // 예약 정보를 삭제하려는 사용자와 관련된 예약 정보를 찾기
        Optional<Reservation> reservationOptional = reservationRepository.findByIdAdminIdAndIdMemberId(id.getAdminId(), id.getMemberId());

        if (reservationOptional.isPresent()) {
            // 예약 정보를 찾았을 경우 삭제
            reservationRepository.delete(reservationOptional.get());
            return RsData.of("S-2", "예약 정보가 삭제되었습니다.");
        } else {
            return RsData.of("F-5", "예약 정보를 찾을 수 없습니다.");
        }
    }

    public RsData<String> checkDuplicateReservation(Long adminId, Long memberId) {
        // 예약 정보를 조회하고 중복 예약 확인
        boolean isDuplicateReservation = reservationRepository.existsByAdminIdAndMemberId(adminId, memberId);

        if (isDuplicateReservation) {
            return RsData.of("F-4", "이미 예약한 병원입니다.");
        }

        return RsData.of("S-3", "예약 가능한 병원입니다.");
    }

    @Transactional
    public RsData<Reservation> updateStatus(Reservation reservation, ReservationStatus status){
        reservation.setStatus(status);
        reservationRepository.save(reservation);
        return RsData.of("S-1", "예약 상태가 업데이트 되었습니다.", reservation);
    }

    @Transactional
    public RsData<Reservation> updateRegisterStatus(Reservation reservation, RegisterChartStatus registerStatus){
        reservation.setRegisterStatus(registerStatus);
        reservationRepository.save(reservation);
        return RsData.of("S-1", "진료 상태가 업데이트 되었습니다.", reservation);
    }

}