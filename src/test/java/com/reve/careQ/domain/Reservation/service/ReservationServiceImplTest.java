package com.reve.careQ.domain.Reservation.service;

import com.reve.careQ.domain.Admin.entity.Admin;
import com.reve.careQ.domain.Admin.service.AdminService;
import com.reve.careQ.domain.Member.entity.Member;
import com.reve.careQ.domain.Member.service.MemberService;
import com.reve.careQ.domain.RegisterChart.exception.EntityNotFoundException;
import com.reve.careQ.domain.Reservation.entity.Reservation;
import com.reve.careQ.global.rsData.RsData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@WithMockUser(username="useruser1", roles={"USER"})
public class ReservationServiceImplTest {

    @Autowired
    private ReservationService reservationService;
    @Autowired
    private AdminService adminService;
    @Autowired
    private MemberService memberService;

    private long hospitalId;
    private long subjectId;
    private Member member;
    private Admin admin;

    @BeforeEach
    public void setUp() {
        hospitalId = 1L;
        subjectId = 1L;
        member = memberService.getCurrentUser().orElseThrow(() -> new NoSuchElementException("로그인한 사용자를 찾을 수 없습니다."));
        admin = adminService.findByHospitalIdAndSubjectId(hospitalId, subjectId)
                .orElseThrow(() -> new EntityNotFoundException("해당 병원과 진료 과목에 해당하는 관리자를 찾을 수 없습니다."));
    }

    @Test
    @DisplayName("멤버 아이디를 사용하여 논리 삭제되지 않은 예약 목록을 조회한다.")
    public void findByMemberIdAndIsDeletedFalseTest() {
        Long memberId = member.getId();

        // 멤버 예약 생성
        reservationService.createReservation(hospitalId, subjectId, "2024-02-01", "10:00:00");

        List<Reservation> reservations = reservationService.findByMemberIdAndIsDeletedFalse(memberId);

        assertNotNull(reservations);
        assertFalse(reservations.isEmpty());
        assertTrue(reservations.stream().allMatch(reservation -> !reservation.getIsDeleted()));
    }

    @Test
    @DisplayName("관리자와 멤버 아이디를 이용하여 예약을 조회한다.")
    public void findByAdminIdAndMemberIdTest() {
        Long adminId = admin.getId();
        Long memberId = member.getId();

        // 멤버 예약 생성
        reservationService.createReservation(hospitalId, subjectId, "2024-02-01", "10:00:00");

        Optional<Reservation> optionalReservation = reservationService.findByAdminIdAndMemberId(adminId, memberId);

        assertTrue(optionalReservation.isPresent());
        assertEquals(optionalReservation.get().getAdmin().getId(), adminId);
        assertEquals(optionalReservation.get().getMember().getId(), memberId);
    }

    @Test
    @DisplayName("당일 예약 목록을 조회한다.")
    public void getTodayReservationTest() {
        List<Reservation> reservations = reservationService.getTodayReservation(admin);

        assertNotNull(reservations);
        assertTrue(reservations.stream().allMatch(reservation ->
                reservation.getDate()
                        .toLocalDate()
                        .isEqual(LocalDate.now()) &&
                        reservation.getAdmin().equals(admin)
        ));
    }

    @Test
    @DisplayName("예약이 가능한 시간에 예약을 시도하면 예약에 성공한다.")
    public void createReservationTest() {
        Reservation reservation = reservationService.createReservation(hospitalId, subjectId, "2024-02-01", "10:00:00");

        assertNotNull(reservation);
        assertEquals(reservation.getMember(), member);
        assertEquals(reservation.getAdmin(), admin);
    }

    @Test
    @DisplayName("이미 예약된 시간에 예약을 시도했을 경우, 예외를 던진다.")
    public void createReservationWithDuplicateTimeTest() {
        reservationService.createReservation(hospitalId, subjectId, "2024-02-01", "10:00:00");

        assertThrows(RuntimeException.class, () ->
                reservationService.createReservation(hospitalId, subjectId, "2024-02-01", "10:00:00"));
    }

    @Test
    @DisplayName("예약 생성 후 데이터 삽입한다.")
    public void insertTest() {
        String selectedDate = "2024-02-02";
        String selectedTime = "10:00:00";

        RsData<Reservation> result = reservationService.insert(hospitalId, subjectId, selectedDate, selectedTime);

        assertNotNull(result);
        assertEquals("S-1", result.getResultCode());
        assertEquals("예약이 성공적으로 등록되었습니다.", result.getMsg());
        assertNotNull(result.getData());
    }

    @Test
    @DisplayName("중복 예약을 체크하여 예약한 병원이 아닐 경우, 예약에 성공한다.")
    public void checkDuplicateReservationTest() {
        Long adminId = admin.getId();
        Long memberId = member.getId();

        RsData<String> result = reservationService.checkDuplicateReservation(adminId, memberId);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("S-3", result.getResultCode());
        assertEquals("예약 가능한 병원입니다.", result.getMsg());
    }

    @Test
    @DisplayName("이미 예약된 병원/진료과목에 예약을 시도했을 경우, 에러 메시지를 반환한다.")
    public void checkReservationExistsTest() {
        Long adminId = admin.getId();
        Long memberId = member.getId();

        // 이미 예약한 상태
        reservationService.createReservation(hospitalId, subjectId, "2024-02-01", "10:00:00");

        RsData<String> result = reservationService.checkDuplicateReservation(adminId, memberId);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals("F-4", result.getResultCode());
        assertEquals("이미 예약한 병원입니다.", result.getMsg());
    }
}
