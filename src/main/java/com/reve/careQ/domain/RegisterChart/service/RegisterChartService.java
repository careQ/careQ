package com.reve.careQ.domain.RegisterChart.service;

import com.reve.careQ.domain.Admin.entity.Admin;
import com.reve.careQ.domain.Admin.service.AdminService;
import com.reve.careQ.domain.Member.entity.Member;
import com.reve.careQ.domain.Member.service.MemberService;
import com.reve.careQ.domain.RegisterChart.entity.RegisterChart;
import com.reve.careQ.domain.RegisterChart.entity.RegisterChartStatus;
import com.reve.careQ.domain.RegisterChart.repository.RegisterChartRepository;
import com.reve.careQ.domain.Reservation.entity.Reservation;
import com.reve.careQ.domain.Reservation.service.ReservationServiceImpl;
import com.reve.careQ.global.compositePKEntity.CompositePKEntity;
import com.reve.careQ.global.rsData.RsData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class RegisterChartService {

    private final RegisterChartRepository registerChartRepository;

    private final AdminService adminService;
    private final MemberService memberService;

    private final ReservationServiceImpl reservationServiceImpl;

    @Autowired
    public RegisterChartService(RegisterChartRepository registerChartRepository, AdminService adminService, MemberService memberService, ReservationServiceImpl reservationServiceImpl) {
        this.registerChartRepository = registerChartRepository;
        this.adminService = adminService;
        this.memberService = memberService;
        this.reservationServiceImpl = reservationServiceImpl;
    }

    public Optional<RegisterChart> findByIdAdminIdAndIdMemberId(Long adminId, Long memberId){
        return registerChartRepository.findByIdAdminIdAndIdMemberId(adminId, memberId);
    }

    public boolean existsByAdminIdAndMemberId(Long adminId, Long memberId){
        return registerChartRepository.existsByAdminIdAndMemberId(adminId,memberId);
    }
    @Transactional
    public RsData<RegisterChart> insert(Long hospitalId, Long subjectId){
        // 사용자 정보 가져오기
        Optional<Member> currentUserOptional = memberService.getCurrentUser();

        if (currentUserOptional.isPresent()) {
            Member currentUser = currentUserOptional.get();
            Optional<Admin> adminOptional = adminService.findByHospitalIdAndSubjectId(hospitalId, subjectId);

            if (adminOptional.isEmpty()) {
                return RsData.of("F-2", "해당 병원과 진료 과목에 해당하는 관리자를 찾을 수 없습니다.");
            }

            // 중복 줄서기 확인
            boolean isDuplicated = registerChartRepository.existsByAdminIdAndMemberId(adminOptional.get().getId(), currentUser.getId());

            if (isDuplicated) {
                return RsData.of("F-4", "이미 접수되었습니다.");
            }

            Optional<Reservation> reservationOptional = reservationServiceImpl.findByAdminIdAndMemberId(adminOptional.get().getId(), currentUser.getId());

            //당일 예약 확인
            if ((reservationOptional.isPresent()) && (LocalDate.now().isEqual(reservationOptional.get().getDate().toLocalDate()))) {
                return RsData.of("F-5", "당일 예약이 존재하여 줄서기 등록이 불가능합니다.");
            }

            // 가져온 Admin 및 Member 엔티티 설정
            Admin admin = adminOptional.get();
            Member member = currentUser;

            // 복합 키 생성
            CompositePKEntity id = new CompositePKEntity();
            id.setAdminId(admin.getId());
            id.setMemberId(member.getId());

            RegisterChart registerChart = RegisterChart.builder()
                    .id(id)
                    .status(RegisterChartStatus.WAITING)
                    .admin(admin)
                    .member(member)
                    .build();

            registerChartRepository.save(registerChart);

            RegisterChart savedRegisterChart = findByIdAdminIdAndIdMemberId(admin.getId(), member.getId()).get();

            return RsData.of("S-1", "접수 테이블에 삽입되었습니다.", savedRegisterChart);
        }else {
            return RsData.of("F-3", "현재 로그인한 사용자 정보를 가져오지 못했습니다.");
        }
    }


    // 줄서기 정보 삭제
    @Transactional
    public RsData<String> deleteRegister(CompositePKEntity id) {
        Optional<RegisterChart> registerChartOptional = registerChartRepository.findByIdAdminIdAndIdMemberId(id.getAdminId(), id.getMemberId());

        if (registerChartOptional.isPresent()) {
            registerChartRepository.delete(registerChartOptional.get());
            return RsData.of("S-2", "줄서기 정보가 삭제되었습니다.");
        } else {
            return RsData.of("F-5", "줄서기 정보를 찾을 수 없습니다.");
        }
    }

    @Transactional
    public RsData<RegisterChart> updateStatus(RegisterChart registerChart, RegisterChartStatus status){
        registerChart.setStatus(status);
        registerChartRepository.save(registerChart);
        return RsData.of("S-1", "줄서기 상태가 업데이트 되었습니다.", registerChart);
    }
}
