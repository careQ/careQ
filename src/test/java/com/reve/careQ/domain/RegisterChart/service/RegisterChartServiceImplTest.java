package com.reve.careQ.domain.RegisterChart.service;

import com.reve.careQ.domain.Admin.entity.Admin;
import com.reve.careQ.domain.Admin.service.AdminService;
import com.reve.careQ.domain.Member.dto.OnsiteRegisterDto;
import com.reve.careQ.domain.Member.entity.Member;
import com.reve.careQ.domain.Member.service.MemberService;
import com.reve.careQ.domain.RegisterChart.entity.RegisterChart;
import com.reve.careQ.domain.RegisterChart.entity.RegisterChartStatus;
import com.reve.careQ.domain.RegisterChart.exception.EntityNotFoundException;
import com.reve.careQ.domain.Reservation.service.ReservationService;
import com.reve.careQ.global.rsData.RsData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class RegisterChartServiceImplTest {

    @Autowired
    private RegisterChartService registerChartService;
    @Autowired
    private AdminService adminService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private ReservationService reservationService;
    
    private long hospitalId;
    private long subjectId;
    private Member member;
    private Admin admin;

    @BeforeEach
    public void setUp(TestInfo testInfo) {
        // 'registerNewMemberOnsiteTest'는 관리자 테스트이기 때문에 setUp 설정이 안되도록
        if ("registerNewMemberOnsiteTest".equals(testInfo.getTestMethod().orElseThrow().getName())) {
            return;
        }

        hospitalId = 1L;
        subjectId = 1L;
        member = memberService.getCurrentUser().orElseThrow(() -> new NoSuchElementException("로그인한 사용자를 찾을 수 없습니다."));
        admin = adminService.findByHospitalIdAndSubjectId(hospitalId, subjectId)
                .orElseThrow(() -> new EntityNotFoundException("해당 병원과 진료 과목에 해당하는 관리자를 찾을 수 없습니다."));
    }

    @Test
    @WithMockUser(username="useruser1", roles={"USER"})
    @DisplayName("줄서기가 성공적으로 등록된다.")
    public void insertTest() {
        RsData<RegisterChart> result = registerChartService.insert(hospitalId, subjectId);

        assertThat(result.getResultCode()).isEqualTo("S-1");
        assertThat(result.getMsg()).isEqualTo("접수 테이블에 삽입되었습니다.");
        assertThat(result.getData()).isNotNull();
        assertThat(result.getData().getStatus()).isEqualTo(RegisterChartStatus.WAITING);
        assertThat(result.getData().getAdmin().getId()).isEqualTo(admin.getId());
        assertThat(result.getData().getMember()).isEqualTo(member);
        assertThat(result.getData().getIsDeleted()).isEqualTo(false);
    }

    @Test
    @WithMockUser(username="useruser1", roles={"USER"})
    @DisplayName("이미 줄서기를 한 경우 중복 접수는 실패한다.")
    public void insertTestWithDuplicated() {
        // 첫 번째 접수를 시도
        RsData<RegisterChart> result1 = registerChartService.insert(hospitalId, subjectId);

        assertThat(result1.getResultCode()).isEqualTo("S-1");
        assertThat(result1.getMsg()).isEqualTo("접수 테이블에 삽입되었습니다.");

        // 이미 접수된 상태에서 다시 접수를 시도
        RsData<RegisterChart> result2 = registerChartService.insert(hospitalId, subjectId);

        // 이미 접수되었으므로, 실패 메시지가 반환되어야 한다.
        assertThat(result2.getResultCode()).isEqualTo("F-4");
        assertThat(result2.getMsg()).isEqualTo("이미 접수되었습니다.");
    }

    @Test
    @WithMockUser(username="useruser1", roles={"USER"})
    @DisplayName("당일 예약이 존재하는 경우 해당 병원 및 진료과목에서 줄서기는 불가능하다.")
    public void insertTestWithExistingReservation() {
        // 당일 예약 생성
        String selectedDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String selectedTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        reservationService.insert(hospitalId, subjectId, selectedDate, selectedTime);

        // 이미 당일 예약이 있는 상태에서 줄서기 시도
        RsData<RegisterChart> result = registerChartService.insert(hospitalId, subjectId);

        // 이미 당일 예약이 존재하므로, 실패 메시지가 반환되어야 한다.
        assertThat(result.getResultCode()).isEqualTo("F-5");
        assertThat(result.getMsg()).isEqualTo("당일 예약이 존재하여 줄서기 등록이 불가능합니다.");
    }

    @Test
    @WithMockUser(username="useruser1", roles={"USER"})
    @DisplayName("줄서기가 성공적으로 취소된다.")
    public void cancelTest() {
        // 줄서기 등록
        registerChartService.insert(hospitalId, subjectId);

        // 줄서기 취소
        RsData<RegisterChart> result = registerChartService.updateStatusByAdminAndMember(admin, member.getId(), RegisterChartStatus.CANCEL);

        assertThat(result.getResultCode()).isEqualTo("S-1");
        assertThat(result.getMsg()).isEqualTo("줄서기 상태가 업데이트 되었습니다.");
        assertThat(result.getData()).isNotNull();
        assertThat(result.getData().getStatus()).isEqualTo(RegisterChartStatus.CANCEL);
    }

    @Test
    @WithMockUser(username="useruser1", roles={"USER"})
    @DisplayName("줄서기가 성공적으로 완료된다.")
    public void completeTest() {
        // 줄서기 등록
        registerChartService.insert(hospitalId, subjectId);

        // 줄서기 완료
        RsData<RegisterChart> result = registerChartService.updateStatusByAdminAndMember(admin, member.getId(), RegisterChartStatus.COMPLETE);

        assertThat(result.getResultCode()).isEqualTo("S-1");
        assertThat(result.getMsg()).isEqualTo("줄서기 상태가 업데이트 되었습니다.");
        assertThat(result.getData()).isNotNull();
        assertThat(result.getData().getStatus()).isEqualTo(RegisterChartStatus.COMPLETE);
    }

    @Test
    @WithMockUser(username="useruser1", roles={"USER"})
    @DisplayName("이미 취소된 줄서기에 대해서 다시 취소를 시도할 경우, 예외가 발생한다.")
    public void cancelAlreadyCancelledRegisterChartTest() {
        // 줄서기 등록
        registerChartService.insert(hospitalId, subjectId);

        // 줄서기 취소
        registerChartService.updateStatusByAdminAndMember(admin, member.getId(), RegisterChartStatus.CANCEL);

        // 이미 취소된 줄서기에 대해 다시 취소를 시도
        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            registerChartService.updateStatusByAdminAndMember(admin, member.getId(), RegisterChartStatus.CANCEL);
        });

        assertThat(exception.getMessage()).isEqualTo("줄서기 정보를 찾을 수 없습니다.");
    }

    @Test
    @WithMockUser(username="useruser1", roles={"USER"})
    @DisplayName("이미 완료된 줄서기에 대해서 다시 완료를 시도하는 경우, 예외가 발생한다.")
    public void completeAlreadyCompletedRegisterChartTest() {
        // 줄서기 등록
        registerChartService.insert(hospitalId, subjectId);

        // 줄서기 완료
        registerChartService.updateStatusByAdminAndMember(admin, member.getId(), RegisterChartStatus.COMPLETE);

        // 이미 완료된 줄서기에 대해 다시 완료를 시도
        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            registerChartService.updateStatusByAdminAndMember(admin, member.getId(), RegisterChartStatus.COMPLETE);
        });

        assertThat(exception.getMessage()).isEqualTo("줄서기 정보를 찾을 수 없습니다.");
    }

    @Test
    @WithMockUser(username="useruser1", roles={"USER"})
    @DisplayName("findRegisterChart()를 통해 이미 논리 삭제된 줄서기 차트를 다시 찾으려고 할 때 예외를 던진다.")
    public void processRegisterChartTest() {
        // 줄서기 등록
        RsData<RegisterChart> rsData = registerChartService.insert(hospitalId, subjectId);
        RegisterChart registerChart = rsData.getData();

        // 줄서기 논리 삭제 처리
        registerChartService.processRegisterChart(hospitalId, subjectId);

        // 처리 후 줄서기 조회 시, findRegisterChart() 테스트 예외 발생 확인
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            registerChartService.processRegisterChart(hospitalId, subjectId);
        });

        assertEquals("등록 차트를 찾을 수 없습니다.", exception.getMessage());
    }

    @Test
    @WithMockUser(username="adminadmin1", roles={"ADMIN"})
    @DisplayName("관리자는 username과 email을 입력하여 미가입 회원을 현장에서 등록한다.")
    public void registerNewMemberOnsiteTest() {
        // 현장 접수 DTO 생성
        OnsiteRegisterDto onsiteRegisterDto = new OnsiteRegisterDto( "username", "email@example.com");

        // 현장에서 새로운 회원 등록 시도
        registerChartService.registerNewUser(onsiteRegisterDto);

        // 등록 후 회원 정보 확인
        Optional<Member> newMember1 = memberService.findByUsername(onsiteRegisterDto.getUsername());
        assertTrue(newMember1.isPresent());

        Member newMember = newMember1.get();
        assertEquals(onsiteRegisterDto.getUsername(), newMember.getUsername());
        assertEquals(onsiteRegisterDto.getEmail(), newMember.getEmail());
    }
}
