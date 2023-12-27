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

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemberService memberService;

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
        RsData<Member> successResult = memberService.join("careQ", NEW_USERNAME, "password", NEW_EMAIL);

        //then
        assertThat(successResult).satisfies(result -> {
            assertThat(result.getResultCode()).isEqualTo("S-1");
            assertThat(result.getData().getUsername()).isEqualTo(NEW_USERNAME);
        });
    }

    @Test
    @DisplayName("기존 회원이 join을 하면 실패한다.")
    void testJoin_Failure() {
        // 아이디 중복
        // given
        //when
        RsData<Member> failureResult1 = memberService.join("careQ", EXISTING_USERNAME, "password", NEW_EMAIL);
        //then
        assertThat(failureResult1).satisfies(result -> {
            assertThat(result.getResultCode()).isEqualTo("F-1");
            assertThat(result.getData()).isNull();
        });

        //이메일 중복
        // given
        //when
        RsData<Member> failureResult2 = memberService.join("careQ", NEW_USERNAME, "password", EXISTING_EMAIL);
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
        RsData<Member> result = memberService.getCurrentUser();
        // then
        assertThat(result).satisfies(r -> {
            assertThat(r.getResultCode()).isEqualTo("S-3");
            assertThat(r.getData().getUsername()).isEqualTo(EXISTING_USERNAME);
        });
    }

    @Test
    @DisplayName("로그인된 회원의 정보를 가져오는 것을 실패한다.")
    void testGetCurrentUser_Failure() {
        // 로그인이 안 되어 있을 경우
        // given - no authentication set
        // when
        RsData<Member> failureResult1 = memberService.getCurrentUser();
        // then
        assertThat(failureResult1).satisfies(result -> {
            assertThat(result.getResultCode()).isEqualTo("F-3");
            assertThat(result.getData()).isNull();
        });

        // 로그인 상태지만 사용자 정보를 찾을 수 없는 경우
        // given
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(NEW_USERNAME, "1234"));
        // when
        RsData<Member> failureResult2 = memberService.getCurrentUser();
        // then
        assertThat(failureResult2).satisfies(result -> {
            assertThat(result.getResultCode()).isEqualTo("F-4");
            assertThat(result.getData()).isNull();
        });
    }

    // Reservation 복합키 해결 후 작성 예정
//    @Test
//    @DisplayName("회원의 예약정보를 가져온다.")
//    void testGetReservationsForMember_Success() {
//    }

    @Test
    @DisplayName("입력한 사용자 정보가 존재하므로 비밀번호 찾기가 가능하다.")
    void testFindPassword_Success() {
        //given
        //when
        RsData<Member> successResult = memberService.findPassword(EXISTING_USERNAME, EXISTING_EMAIL);
        //then
        assertThat(successResult).satisfies(result -> {
            assertThat(result.getResultCode()).isEqualTo("S-3");
            assertThat(result.getData().getUsername()).isEqualTo(EXISTING_USERNAME);
        });
    }

    @Test
    @DisplayName("비밀번호 찾기를 실패한다.")
    void testFindPassword_Failure() {
        //존재하지 않는 계정
        //given
        //when
        RsData<Member> failureResult1 = memberService.findPassword(NEW_USERNAME, NEW_EMAIL);
        //then
        assertThat(failureResult1).satisfies(result -> {
            assertThat(result.getResultCode()).isEqualTo("F-4");
            assertThat(result.getData()).isNull();
        });

        //소셜로그인과 연동된 계정
        //given
        String socialUsername = "socialMember";
        String socialEmail = "socialMember@naver.com";
        //when
        RsData<Member> failureResult2 = memberService.findPassword(socialUsername, socialEmail);
        //then
        assertThat(failureResult2).satisfies(result -> {
            assertThat(result.getResultCode()).isEqualTo("F-4");
            assertThat(result.getData()).isNull();
        });
    }

}