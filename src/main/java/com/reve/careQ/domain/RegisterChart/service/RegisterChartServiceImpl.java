package com.reve.careQ.domain.RegisterChart.service;

import com.reve.careQ.domain.Admin.entity.Admin;

import com.reve.careQ.domain.Admin.service.AdminService;
import com.reve.careQ.domain.Hospital.entity.Hospital;
import com.reve.careQ.domain.Hospital.service.HospitalService;
import com.reve.careQ.domain.Member.entity.Member;
import com.reve.careQ.domain.Member.service.MemberService;
import com.reve.careQ.domain.RegisterChart.entity.RegisterChart;
import com.reve.careQ.domain.RegisterChart.dto.RegisterChartInfoDto;
import com.reve.careQ.domain.RegisterChart.entity.RegisterChartStatus;;
import com.reve.careQ.domain.RegisterChart.exception.ResourceNotFoundException;
import com.reve.careQ.domain.RegisterChart.repository.RegisterChartRepository;
import com.reve.careQ.domain.Reservation.entity.Reservation;
import com.reve.careQ.domain.Reservation.service.ReservationService;
import com.reve.careQ.domain.Subject.entity.Subject;
import com.reve.careQ.domain.Subject.service.SubjectService;
import com.reve.careQ.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RegisterChartServiceImpl implements RegisterChartService {
    private final RegisterChartRepository registerChartRepository;
    private final AdminService adminService;
    private final MemberService memberService;
    private final ReservationService reservationService;
    private final HospitalService hospitalService;
    private final SubjectService subjectService;

    @Override
    @Transactional(readOnly = true)
    public Optional<RegisterChart> findById(Long id){
        return registerChartRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public RegisterChartInfoDto getRegisterChartInfo(Long hospitalId, Long subjectId){
        Admin admin = findAdmin(hospitalId, subjectId);
        Member currentUser = getCurrentUser();
        RegisterChart registerChart = findRegisterChart(admin, currentUser);
        Subject subject = findSubject(subjectId);
        Hospital hospital = findHospital(hospitalId);

        return RegisterChartInfoDto.of(registerChart, subject, hospital, admin);
    }

    private Admin findAdmin(Long hospitalId, Long subjectId) {
        return adminService.findByHospitalIdAndSubjectId(hospitalId, subjectId)
                .orElseThrow(() -> new IllegalArgumentException("해당 병원과 진료 과목에 해당하는 관리자를 찾을 수 없습니다."));
    }

    private Member getCurrentUser() {
        return memberService.getCurrentUser()
                .orElseThrow(() -> new IllegalArgumentException("현재 로그인한 사용자 정보를 가져오지 못했습니다."));
    }

    private RegisterChart findRegisterChart(Admin admin, Member currentUser) {
        Optional<RegisterChart> registerChart = registerChartRepository.findByAdminIdAndMemberIdAndIsDeletedFalse(admin.getId(), currentUser.getId());
        return registerChart.orElse(null);
    }

    private Subject findSubject(Long subjectId) {
        return subjectService.findById(subjectId).orElseThrow(() -> new ResourceNotFoundException("진료 과목을 찾을 수 없습니다."));
    }

    private Hospital findHospital(Long hospitalId) {
        return hospitalService.findById(hospitalId).orElseThrow(() -> new ResourceNotFoundException("해당 병원을 찾을 수 없습니다."));
    }

    @Override
    @Transactional
    public RsData<RegisterChart> insert(Long hospitalId, Long subjectId){
        Member currentUser = memberService.getCurrentUser()
                .orElseThrow(() -> new IllegalArgumentException("현재 로그인한 사용자 정보를 가져오지 못했습니다."));

        return registerToChart(currentUser, hospitalId, subjectId);
    }

    private RsData<RegisterChart> registerToChart(Member currentUser, Long hospitalId, Long subjectId) {
        Admin admin = adminService.findByHospitalIdAndSubjectId(hospitalId, subjectId)
                .orElseThrow(() -> new IllegalArgumentException("해당 병원과 진료 과목에 해당하는 관리자를 찾을 수 없습니다."));

        return checkForDuplicatesAndInsert(currentUser, admin);
    }

    private RsData<RegisterChart> checkForDuplicatesAndInsert(Member currentUser, Admin admin) {
        boolean isDuplicated = registerChartRepository.existsByAdminIdAndMemberIdAndIsDeletedFalse(admin.getId(), currentUser.getId());

        if (isDuplicated) {
            return RsData.of("F-4", "이미 접수되었습니다.");
        }

        return checkForReservationsAndInsert(currentUser, admin);
    }

    private RsData<RegisterChart> checkForReservationsAndInsert(Member currentUser, Admin admin) {
        Optional<Reservation> reservationOptional = reservationService.findByAdminIdAndMemberId(admin.getId(), currentUser.getId());

        if ((reservationOptional.isPresent()) && (LocalDate.now().isEqual(reservationOptional.get().getDate().toLocalDate()))) {
            return RsData.of("F-5", "당일 예약이 존재하여 줄서기 등록이 불가능합니다.");
        }

        return insertRegisterChart(currentUser, admin);
    }

    private RsData<RegisterChart> insertRegisterChart(Member currentUser, Admin admin) {
        RegisterChart registerChart = RegisterChart.builder()
                .status(RegisterChartStatus.WAITING)
                .admin(admin)
                .member(currentUser)
                .isDeleted(false)
                .build();

        RegisterChart savedRegisterChart = registerChartRepository.save(registerChart);

        return RsData.of("S-1", "접수 테이블에 삽입되었습니다.", savedRegisterChart);
    }

    @Override
    @Transactional
    public RsData<RegisterChart> updateStatusByAdminAndMember(Admin admin, Long memberId, RegisterChartStatus status) {
        RegisterChart registerChart = registerChartRepository.findRegisterChartByAdminIdAndMemberIdAndIsDeletedFalse(admin.getId(), memberId)
                .orElseThrow(() -> new IllegalArgumentException("줄서기 정보를 찾을 수 없습니다."));

        if(status.equals(RegisterChartStatus.CANCEL) || status.equals(RegisterChartStatus.COMPLETE)){
            registerChart.markAsDeleted(true);
            registerChart.setStatus(status);
        } else {
            registerChart.setStatus(status);
        }

        registerChartRepository.save(registerChart);
        return RsData.of("S-1", "줄서기 상태가 업데이트 되었습니다.", registerChart);
    }

    @Override
    @Transactional
    public void processRegisterChart(Long hospitalId, Long subjectId) {
        RegisterChart registerChart = findRegisterChart(hospitalId, subjectId);
        deleteRegisterChart(registerChart);
    }

    private RegisterChart findRegisterChart(Long hospitalId, Long subjectId) {
        Admin admin = adminService.findByHospitalIdAndSubjectId(hospitalId, subjectId)
                .orElseThrow(() -> new IllegalArgumentException("해당 병원과 진료 과목에 해당하는 관리자를 찾을 수 없습니다."));

        Member currentUser = memberService.getCurrentUser()
                .orElseThrow(() -> new IllegalArgumentException("현재 로그인한 사용자 정보를 가져오지 못했습니다."));

        return registerChartRepository.findByAdminIdAndMemberIdAndIsDeletedFalse(admin.getId(), currentUser.getId())
                .orElseThrow(() -> new IllegalArgumentException("등록 차트를 찾을 수 없습니다."));
    }

    private void deleteRegisterChart(RegisterChart registerChart) {
        registerChart.markAsDeleted(true);
        if(registerChart.getStatus().equals(RegisterChartStatus.CANCEL) || registerChart.getStatus().equals(RegisterChartStatus.COMPLETE)){
            registerChart.setStatus(registerChart.getStatus());
        } else {
            registerChart.setStatus(RegisterChartStatus.CANCEL);
        }
        registerChartRepository.save(registerChart);
    }

    @Override
    public RegisterChart registerNewMember(String providerType, String username, String tempPassword, String email) {
        Admin currentAdmin = adminService.getCurrentAdmin()
                .orElseThrow(() -> new RuntimeException("로그인한 관리자가 아닙니다."));

        RsData<Member> validation = memberService.validateJoinRequest(providerType, username, email);
        if (!validation.isSuccess()) {
            throw new RuntimeException(validation.getMsg());
        }

        Member newMember = memberService.createMember(providerType, username, tempPassword, email);

        RegisterChart newRegisterChart = RegisterChart.builder()
                .status(RegisterChartStatus.WAITING)
                .admin(currentAdmin)
                .member(newMember)
                .isDeleted(false)
                .build();

        return registerChartRepository.save(newRegisterChart);
    }
}
