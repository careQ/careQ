package com.reve.careQ.domain.RegisterChart.service;

import com.reve.careQ.domain.Admin.entity.Admin;

import com.reve.careQ.domain.Admin.service.AdminService;
import com.reve.careQ.domain.Hospital.entity.Hospital;
import com.reve.careQ.domain.Hospital.service.HospitalService;
import com.reve.careQ.domain.Member.dto.OnsiteRegisterDto;
import com.reve.careQ.domain.Member.entity.Member;
import com.reve.careQ.domain.Member.service.MemberService;
import com.reve.careQ.domain.RegisterChart.entity.RegisterChart;
import com.reve.careQ.domain.RegisterChart.dto.RegisterChartInfoDto;
import com.reve.careQ.domain.RegisterChart.entity.RegisterChartStatus;;
import com.reve.careQ.domain.RegisterChart.exception.EntityNotFoundException;
import com.reve.careQ.domain.RegisterChart.repository.RegisterChartRepository;
import com.reve.careQ.domain.Reservation.entity.Reservation;
import com.reve.careQ.domain.Reservation.service.ReservationService;
import com.reve.careQ.domain.Subject.entity.Subject;
import com.reve.careQ.domain.Subject.service.SubjectService;
import com.reve.careQ.global.rq.Rq;
import com.reve.careQ.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegisterChartServiceImpl implements RegisterChartService {
    private final RegisterChartRepository registerChartRepository;
    private final AdminService adminService;
    private final MemberService memberService;
    private final ReservationService reservationService;
    private final HospitalService hospitalService;
    private final SubjectService subjectService;
    private final Rq rq;

    @Override
    @Transactional(readOnly = true)
    public Optional<RegisterChart> findById(Long id){
        return registerChartRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public RegisterChartInfoDto getRegisterChartInfo(Long hospitalId, Long subjectId) {
        Admin admin = findAdmin(hospitalId, subjectId);
        Member currentUser = getCurrentUser();
        RegisterChart registerChart = findRegisterChart(admin, currentUser);
        Subject subject = findSubject(subjectId);
        Hospital hospital = findHospital(hospitalId);

        Long waitingCount = calculateWaitingCount(hospitalId, subjectId, registerChart);
        String waitingStatus = waitingStatus(waitingCount, registerChart);

        return RegisterChartInfoDto.of(registerChart, subject, hospital, admin, waitingCount, calculateExpectedWaitingTime(waitingCount), waitingStatus);
    }

    private Long calculateWaitingCount(Long hospitalId, Long subjectId, RegisterChart registerChart) {
        Long count = registerChartRepository.countByAdminHospitalIdAndAdminSubjectIdAndStatusNotIn(hospitalId, subjectId, Arrays.asList(RegisterChartStatus.CANCEL, RegisterChartStatus.COMPLETE, RegisterChartStatus.CANCELLED));
        return isRegisterChartWaitingOrEnter(registerChart) ? count - 1 : count;
    }

    private String waitingStatus(Long waitingCount, RegisterChart registerChart) {
        return isRegisterChartWaitingOrEnter(registerChart) ? (waitingCount == 0 ? "내 차례" : waitingCount.toString()) : waitingCount.toString();
    }

    private Long calculateExpectedWaitingTime(Long waitingCount) {
        return waitingCount * 5;
    }

    private boolean isRegisterChartWaitingOrEnter(RegisterChart registerChart) {
        return registerChart != null && (registerChart.getStatus() == RegisterChartStatus.WAITING || registerChart.getStatus() == RegisterChartStatus.ENTER);
    }

    private Admin findAdmin(Long hospitalId, Long subjectId) {
        return getEntity(adminService.findByHospitalIdAndSubjectId(hospitalId, subjectId), "해당 병원과 진료 과목에 해당하는 관리자를 찾을 수 없습니다.");
    }

    private Member getCurrentUser() {
        return getEntity(memberService.getCurrentUser(), "현재 로그인한 사용자 정보를 가져오지 못했습니다.");
    }

    private RegisterChart findRegisterChart(Admin admin, Member currentUser) {
        Optional<RegisterChart> registerChart = registerChartRepository.findByAdminIdAndMemberIdAndIsDeletedFalse(admin.getId(), currentUser.getId());
        return registerChart.orElse(null);
    }

    private Subject findSubject(Long subjectId) {
        return getEntity(subjectService.findById(subjectId), "진료 과목을 찾을 수 없습니다.");
    }

    private Hospital findHospital(Long hospitalId) {
        return getEntity(hospitalService.findById(hospitalId), "해당 병원을 찾을 수 없습니다.");
    }

    private <T> T getEntity(Optional<T> optionalEntity, String errorMessage) {
        return optionalEntity.orElseThrow(() -> new EntityNotFoundException(errorMessage));
    }

    @Override
    @Transactional
    public RsData<RegisterChart> insert(Long hospitalId, Long subjectId){
        Member currentUser = getCurrentUser();
        return registerToChart(currentUser, hospitalId, subjectId);
    }

    private RsData<RegisterChart> registerToChart(Member currentUser, Long hospitalId, Long subjectId) {
        Admin admin = findAdmin(hospitalId, subjectId);
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
        RegisterChart registerChart = getEntity(registerChartRepository.findRegisterChartByAdminIdAndMemberIdAndIsDeletedFalse(admin.getId(), memberId), "줄서기 정보를 찾을 수 없습니다.");

        if(isStatusCancelOrComplete(status)){
            registerChart.markAsDeleted(true);
        }

        registerChart.setStatus(status);
        registerChartRepository.save(registerChart);
        return RsData.of("S-1", "줄서기 상태가 업데이트 되었습니다.", registerChart);
    }

    private boolean isStatusCancelOrComplete(RegisterChartStatus status) {
        return status.equals(RegisterChartStatus.CANCEL) || status.equals(RegisterChartStatus.COMPLETE);
    }

    @Override
    @Transactional
    public void processRegisterChart(Long hospitalId, Long subjectId) {
        RegisterChart registerChart = findRegisterChart(hospitalId, subjectId);
        deleteRegisterChart(registerChart);
    }

    private RegisterChart findRegisterChart(Long hospitalId, Long subjectId) {
        Admin admin = findAdmin(hospitalId, subjectId);
        Member currentUser = getCurrentUser();

        return getEntity(registerChartRepository.findByAdminIdAndMemberIdAndIsDeletedFalse(admin.getId(), currentUser.getId()), "등록 차트를 찾을 수 없습니다.");
    }

    private void deleteRegisterChart(RegisterChart registerChart) {
        registerChart.markAsDeleted(true);

        if(!isStatusCancelOrComplete(registerChart.getStatus())){
            registerChart.setStatus(RegisterChartStatus.CANCEL);
        }

        registerChartRepository.save(registerChart);
    }

    @Override
    @Transactional
    public RegisterChart registerNewMember(String providerType, String username, String tempPassword, String email) {
        Admin currentAdmin = getEntity(adminService.getCurrentAdmin(), "로그인한 관리자가 아닙니다.");
        Member newMember = validateAndCreateMember(providerType, username, email, tempPassword);
        return createAndSaveRegisterChart(currentAdmin, newMember);
    }

    @Override
    @Transactional
    public void registerNewUser(OnsiteRegisterDto onsiteRegisterDto) {
        registerNewMember("careQ", onsiteRegisterDto.getUsername(), generateTempPassword(), onsiteRegisterDto.getEmail());
    }

    private String generateTempPassword() {
        return UUID.randomUUID().toString();
    }

    private Member validateAndCreateMember(String providerType, String username, String email, String tempPassword) {
        validateJoinRequest(providerType, username, email);
        return memberService.createMember(providerType, username, tempPassword, email);
    }

    private void validateJoinRequest(String providerType, String username, String email) {
        validate(providerType, username, true);
        validate(providerType, email, false);
    }

    private void validate(String providerType, String value, boolean isUsername) {
        RsData<Member> validation = memberService.validateJoinRequest(providerType, isUsername ? value : null, isUsername ? null : value);

        if (!validation.isSuccess()) {
            throw new RuntimeException(validation.getMsg());
        }
    }

    private RegisterChart createAndSaveRegisterChart(Admin currentAdmin, Member newMember) {
        RegisterChart newRegisterChart = RegisterChart.builder()
                .status(RegisterChartStatus.WAITING)
                .admin(currentAdmin)
                .member(newMember)
                .isDeleted(false)
                .build();
        return registerChartRepository.save(newRegisterChart);
    }
}
