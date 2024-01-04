package com.reve.careQ.domain.Member.service;

import com.reve.careQ.domain.Member.entity.Member;
import com.reve.careQ.domain.Member.repository.MemberRepository;
import com.reve.careQ.global.rsData.RsData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MemberServiceImplTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemberServiceImpl memberServiceImpl;

    private final String EXISTING_USERNAME = "useruser1";
    private final String EXISTING_EMAIL = "user1@test.com";
    private final String NEW_USERNAME = "newUser";
    private final String NEW_EMAIL = "newUser@test.com";

    @BeforeEach
    void setUp() {
        Member socialLoginMember = Member.builder()
                .username("socialMember")
                .password("oldPassword")
                .email("socialMember@naver.com")
                .providerTypeCode("naver")
                .build();

        memberRepository.save(socialLoginMember);
    }


    @Test
    @DisplayName("새로운 회원이 join을 하면 성공한다.")
    void testJoin_Success() {
        // given
        //when
        RsData<Member> successResult = memberServiceImpl.join("careQ", NEW_USERNAME, "password", NEW_EMAIL);

        //then
        assertThat(successResult).satisfies(result -> {
            assertThat(result.getResultCode()).isEqualTo("S-1");
            assertThat(result.getData().getUsername()).isEqualTo(NEW_USERNAME);
        });
    }

    @Test
    @DisplayName("아이디가 중복된 경우 기존 회원이 join을 하면 실패한다.")
    void testJoin_Failure() {
        // 아이디 중복
        // given
        //when
        RsData<Member> failureResult1 = memberServiceImpl.join("careQ", EXISTING_USERNAME, "password", NEW_EMAIL);
        //then
        assertThat(failureResult1).satisfies(result -> {
            assertThat(result.getResultCode()).isEqualTo("F-1");
            assertThat(result.getData()).isNull();
        });
    }

    @Test
    @DisplayName("이메일이 중복된 경우 기존 회원이 join을 하면 실패한다.")
    void testJoin_Failure_2() {
        //이메일 중복
        // give
        //when
        RsData<Member> failureResult2 = memberServiceImpl.join("careQ", NEW_USERNAME, "password", EXISTING_EMAIL);
        //then
        assertThat(failureResult2).satisfies(result -> {
            assertThat(result.getResultCode()).isEqualTo("F-2");
            assertThat(result.getData()).isNull();
        });
    }

    @Test
    @DisplayName("로그인된 회원의 정보를 가져오는 것을 성공한다.")
    void testGetCurrentUser_Success() {
        // given
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(EXISTING_USERNAME, "1234"));
        // when
        Optional<Member> currentUserOptional = memberServiceImpl.getCurrentUser();

        // then
        assertTrue(currentUserOptional.isPresent());
        assertEquals(EXISTING_USERNAME, currentUserOptional.get().getUsername());
    }

    @Test
    @DisplayName("로그인이 안 되어 있는 경우 로그인된 회원의 정보를 가져오는 것을 실패한다.")
    void testGetCurrentUser_Failure() {
        // 로그인이 안 되어 있을 경우
        // given - no authentication set
        // TODO: 시나리오 수정
        assertThrows(RuntimeException.class, () -> {
            memberServiceImpl.getCurrentUser();
        }, "인증되지 않은 사용자입니다.");
    }

    @Test
    @DisplayName("로그인 상태지만 사용자 정보를 찾을 수 없는 경우 로그인된 회원의 정보를 가져오는 것을 실패한다.")
    void testGetCurrentUser_Failure_2(){
        // 로그인 상태지만 사용자 정보를 찾을 수 없는 경우
        // given
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(NEW_USERNAME, "1234"));
        // when
        Optional<Member> currentUserOptional = memberServiceImpl.getCurrentUser();
        // then
        assertFalse(currentUserOptional.isPresent());
    }

    // TODO: Reservation 복합키 해결 후 작성 예정
//    @Test
//    @DisplayName("회원의 예약정보를 가져온다.")
//    void testGetReservationsForMember_Success() {
//    }

    @Test
    @DisplayName("입력한 사용자 정보가 존재하므로 비밀번호 찾기가 가능하다.")
    void testFindPassword_Success() {
        //given
        //when
        RsData<Member> successResult = memberServiceImpl.findPassword(EXISTING_USERNAME, EXISTING_EMAIL);
        //then
        assertThat(successResult).satisfies(result -> {
            assertThat(result.getResultCode()).isEqualTo("S-3");
            assertThat(result.getData().getUsername()).isEqualTo(EXISTING_USERNAME);
        });
    }

    @Test
    @DisplayName("계정이 존재하지 않을 경우 비밀번호 찾기를 실패한다.")
    void testFindPassword_Failure() {
        //계정이 존재하지 않을 경우
        //given
        //when
        RsData<Member> failureResult1 = memberServiceImpl.findPassword(NEW_USERNAME, NEW_EMAIL);
        //then
        assertThat(failureResult1).satisfies(result -> {
            assertThat(result.getResultCode()).isEqualTo("F-4");
            assertThat(result.getData()).isNull();
        });
    }

    @Test
    @DisplayName("소셜로그인과 연동된 계정일 경우 비밀번호 찾기를 실패한다.")
    void testFindPassword_Failure_2() {
        //소셜로그인과 연동된 계정일 경우
        //given
        String socialUsername = "socialMember";
        String socialEmail = "socialMember@naver.com";
        //when
        RsData<Member> failureResult2 = memberServiceImpl.findPassword(socialUsername, socialEmail);
        //then
        assertThat(failureResult2).satisfies(result -> {
            assertThat(result.getResultCode()).isEqualTo("F-4");
            assertThat(result.getData()).isNull();
        });
    }

}


