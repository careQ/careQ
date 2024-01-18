package com.reve.careQ.domain.Admin.service;

import com.reve.careQ.domain.Admin.entity.Admin;
import com.reve.careQ.domain.RegisterChart.dto.RegisterChartDto;
import com.reve.careQ.domain.Reservation.entity.Reservation;
import com.reve.careQ.global.rsData.RsData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class AdminServiceImplTest {

    @Autowired
    private AdminService adminService;

    private final String ADMIN_USERNAME = "adminadmin1";
    private final String NEW_USERNAME = "newadminadmin";
    private final String NEW_EMAIL = "newEmail@test.com";

    @Test
    @WithMockUser(username="adminadmin1")
    @DisplayName("로그인된 관리자의 정보를 가져오는 것을 성공한다.")
    void testGetCurrentAdmin_Success() {
        // given
        // when
        Optional<Admin> currentAdminOptional = adminService.getCurrentAdmin();
        // then
        assertTrue(currentAdminOptional.isPresent());
        assertEquals(ADMIN_USERNAME, currentAdminOptional.get().getUsername());
    }

    @Test
    @DisplayName("로그인이 안 되어 있는 경우 로그인된 관리자의 정보를 가져오는 것을 실패한다.")
    void testGetCurrentAdmin_Failure() {
        // 로그인이 안 되어 있을 경우
        // given - no authentication set
        // when
        Throwable exception = assertThrows(RuntimeException.class, () -> {
            adminService.getCurrentAdmin();
        });
        //t hen
        assertEquals("인증되지 않은 관리자입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("새로운 관리자가 join을 하면 성공한다.")
    void testJoin_Success() {
        // given
        String hospitalCode = "A0000028";
        String subjectCode = "D003";
        // when
        RsData<Admin> successResult = adminService.join(hospitalCode, subjectCode, NEW_USERNAME, "password", NEW_EMAIL);
        // then
        assertThat(successResult).satisfies(result -> {
            assertThat(result.getResultCode()).isEqualTo("S-1");
            assertThat(result.getData().getUsername()).isEqualTo(NEW_USERNAME);
        });
    }

    @Test
    @DisplayName("이미 등록된 병원코드와 과목코드의 관리자인 경우 join을 실패한다.")
    void testJoin_Failure() {
        // 이미 등록된 병원코드와 과목코드의 관리자인 경우
        // given
        String hospitalCode = "A0000028";
        String subjectCode = "D001";
        // when
        RsData<Admin> failureResult1 = adminService.join(hospitalCode, subjectCode, NEW_USERNAME, "password", NEW_EMAIL);
        // then
        assertThat(failureResult1).satisfies(result -> {
            assertThat(result.getResultCode()).isEqualTo("F-1");
            assertThat(result.getMsg()).isEqualTo("%s %s 관리자는 이미 사용중입니다.".formatted(hospitalCode, subjectCode));
        });
    }

    @Test
    @DisplayName("아이디가 중복된 경우 join을 실패한다.")
    void testJoin_Failure_2() {
        // 아이디가 중복된 경우
        // given
        String hospitalCode = "A0000028";
        String subjectCode = "D003";
        String existingUsername = ADMIN_USERNAME;
        // when
        RsData<Admin> failureResult2 = adminService.join(hospitalCode, subjectCode, existingUsername, "password", NEW_EMAIL);
        // then
        assertThat(failureResult2).satisfies(result -> {
            assertThat(result.getResultCode()).isEqualTo("F-2");
            assertThat(result.getMsg()).isEqualTo("해당 아이디(%s)는 이미 사용중입니다.".formatted(existingUsername));
        });
    }

    @Test
    @DisplayName("이메일이 중복된 경우 join을 실패한다.")
    void testJoin_Failure_3() {
        // 이메일이 중복된 경우
        // given
        String hospitalCode = "A0000028";
        String subjectCode = "D003";
        String existingEmail = "admin1@test.com";
        // when
        RsData<Admin> failureResult3 = adminService.join(hospitalCode, subjectCode, NEW_USERNAME, "password", existingEmail);
        // then
        assertThat(failureResult3).satisfies(result -> {
            assertThat(result.getResultCode()).isEqualTo("F-3");
            assertThat(result.getMsg()).isEqualTo("해당 이메일(%s)은 이미 사용중입니다.".formatted(existingEmail));
        });
    }

    @Test
    @DisplayName("존재하지 않은 과목코드를 입력한 경우 join을 실패한다.")
    void testJoin_Failure_4() {
        // 존재하지 않은 과목코드를 입력한 경우
        // given
        String hospitalCode = "A0000028";
        String subjectCode = "D000";
        // when
        RsData<Admin> failureResult4 = adminService.join(hospitalCode, subjectCode, NEW_USERNAME, "password", NEW_EMAIL);
        // then
        assertThat(failureResult4).satisfies(result -> {
            assertThat(result.getResultCode()).isEqualTo("F-4");
            assertThat(result.getMsg()).isEqualTo("해당 과목코드(%s)는 존재하지 않습니다.".formatted(subjectCode));
        });
    }

    @Test
    @DisplayName("존재하지 않은 병원코드를 입력한 경우 join을 실패한다.")
    void testJoin_Failure_5() {
        // 존재하지 않은 병원코드를 입력한 경우
        // given
        String hospitalCode = "A0000000";
        String subjectCode = "D001";
        // when
        RsData<Admin> failureResult5 = adminService.join(hospitalCode, subjectCode, NEW_USERNAME, "password", NEW_EMAIL);
        // then
        assertThat(failureResult5).satisfies(result -> {
            assertThat(result.getResultCode()).isEqualTo("F-5");
            assertThat(result.getMsg()).isEqualTo("존재하지 않는 병원코드 입니다.");
        });
    }

    @Test
    @DisplayName("입력한 병원명과 과목명을 가진 관리자 찾기에 성공한다.")
    void testFindAdmin_Success(){
        // given
        String hospitalName = "세브란스병원";
        String subjectName = "내과";
        // when
        RsData<Admin> successResult = adminService.findAdmin(subjectName, hospitalName);
        // then
        assertThat(successResult).satisfies(result -> {
            assertThat(result.getResultCode()).isEqualTo("S-1");
            assertThat(result.getMsg()).isEqualTo("해당 관리자가 존재합니다.");
        });
    }

    @Test
    @DisplayName("입력한 병원명과 과목명을 가진 관리자 찾기에 실패한다.")
    void testFindAdmin_Failure(){
        // 입력한 병원명과 과목명에 대한 병원과 과목이 존재하지만 관리자가 없는 경우
        // given
        String hospitalName = "세브란스병원";
        String subjectName = "신경과";
        // when
        RsData<Admin> failureResult1 = adminService.findAdmin(subjectName, hospitalName);
        // then
        assertThat(failureResult1).satisfies(result -> {
            assertThat(result.getResultCode()).isEqualTo("F-1");
            assertThat(result.getMsg()).isEqualTo("해당 관리자가 존재하지 않습니다.\n다른 병원으로 검색해주세요.");
        });
    }

    @Test
    @DisplayName("입력한 과목명에 해당하는 과목이 존재하지 않는 경우 관리자 찾기에 실패한다.")
    void testFindAdmin_Failure_2(){
        // 입력한 과목명에 해당하는 과목이 존재하지 않는 경우
        // given
        String hospitalName = "세브란스병원";
        String subjectName = "notexist";
        // when
        RsData<Admin> failureResult2 = adminService.findAdmin(subjectName, hospitalName);
        // then
        assertThat(failureResult2).satisfies(result -> {
            assertThat(result.getResultCode()).isEqualTo("F-2");
            assertThat(result.getMsg()).isEqualTo("해당 진료과목(%s)은 존재하지 않습니다.\n진료과목명을 정확하게 입력해주세요.".formatted(subjectName));
        });
    }

    @Test
    @DisplayName("입력한 병원명에 해당하는 병원이 존재하지 않는 경우 관리자 찾기에 실패한다.")
    void testFindAdmin_Failure_3(){
        // 입력한 병원명에 해당하는 병원이 존재하지 않는 경우
        // given
        String hospitalName = "notexist";
        String subjectName = "내과";
        // when
        RsData<Admin> failureResult3 = adminService.findAdmin(subjectName, hospitalName);
        // then
        assertThat(failureResult3).satisfies(result -> {
            assertThat(result.getResultCode()).isEqualTo("F-3");
            assertThat(result.getMsg()).isEqualTo("해당 병원명(%s)은 존재하지 않습니다.\n 병원명을 정확하게 입력해주세요.".formatted(hospitalName));
        });
    }

    @Test
    @WithMockUser(username="adminadmin1")
    @DisplayName("로그인된 관리자의 정보에 대한 예약정보를 가져오는데에 성공한다.")
    void testGetReservationsForCurrentAdmin_Success() {
        // given
        // when
        List<Reservation> reservations = adminService.getReservationsForCurrentAdmin();
        // then
        assertTrue(reservations.stream().allMatch(reservation ->
                        reservation.getAdmin().getUsername().equals("adminadmin1")
        ));
    }

    @Test
    @DisplayName("로그인된 관리자의 정보에 대한 예약정보를 가져오는데에 실패한다.")
    void testGetReservationsForCurrentAdmin_Failure() {
        // given
        // when
        Throwable exception = assertThrows(RuntimeException.class, () -> {
            adminService.getReservationsForCurrentAdmin();
        });
        //then
        assertEquals("인증되지 않은 관리자입니다.", exception.getMessage());
    }

    @Test
    @WithMockUser(username="adminadmin1")
    @DisplayName("입력된 아이디에 대한 줄서기정보를 가져오는데에 성공한다.")
    void testGetRegisterChartDtoByMemberName_Success() {
        // given
        String name = "";
        // when
        List<RegisterChartDto> RegisterChartDtos = adminService.getRegisterChartDtoByMemberName(name);
        // then
        assertTrue(RegisterChartDtos.stream().allMatch(RegisterChartDto ->
                RegisterChartDto.getAdminId().equals((long)1)
        ));
    }

    @Test
    @DisplayName("입력된 아이디에 대한 줄서기정보를 가져오는데에 실패한다.")
    void testGetRegisterChartDtoByMemberName_Failure() {
        // given
        String name = "";
        // when
        Throwable exception = assertThrows(RuntimeException.class, () -> {
            adminService.getRegisterChartDtoByMemberName(name);
        });
        //then
        assertEquals("인증되지 않은 관리자입니다.", exception.getMessage());
    }
}