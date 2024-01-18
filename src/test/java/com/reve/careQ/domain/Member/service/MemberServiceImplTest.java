package com.reve.careQ.domain.Member.service;

import com.reve.careQ.domain.Member.entity.Member;
import com.reve.careQ.domain.Member.repository.MemberRepository;
import com.reve.careQ.global.mail.EmailException;
import com.reve.careQ.global.mail.TempPasswordMail;
import com.reve.careQ.global.rsData.RsData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
class MemberServiceImplTest {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private MemberServiceImpl memberServiceImpl;
    @Mock
    private TempPasswordMail tempPasswordMail;

    private final String EXISTING_USERNAME = "useruser1";
    private final String EXISTING_EMAIL = "user1@test.com";
    private final String EXISTING_PASSWORD = "1234";
    private final String NEW_USERNAME = "newUser";
    private final String NEW_EMAIL = "newUser@test.com";
    private final String NEW_PASSWORD = "newPassword";

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
    @WithMockUser(username="useruser1")
    @DisplayName("로그인된 회원의 정보를 가져오는 것을 성공한다.")
    void testGetCurrentUser_Success() {
        // given
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
        //when
        Throwable exception = assertThrows(RuntimeException.class, () -> {
            memberServiceImpl.getCurrentUser();
        });
        //then
        assertEquals("인증되지 않은 사용자입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("입력한 사용자 정보가 존재하므로 비밀번호 찾기를 성공한다.")
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

    @Test
    @DisplayName("새 비밀번호 발급 후 이메일 전송을 성공한다.")
    void testModifyPassword_Success() throws EmailException {
        // given
        memberServiceImpl = new MemberServiceImpl(passwordEncoder, memberRepository, tempPasswordMail);

        // when
        memberServiceImpl.modifyPassword(EXISTING_EMAIL);

        // then
        verify(tempPasswordMail).sendSimpleMessage(eq(EXISTING_EMAIL), anyString());
    }

    @Test
    @DisplayName("이메일이 존재하지 않는 경우 새 비밀번호 발급 후 이메일 전송에 실패한다.")
    void testModifyPassword_Failure() {
        //given
        //when
        Throwable exception = assertThrows(RuntimeException.class, () -> {
            memberServiceImpl.modifyPassword(NEW_EMAIL);
        });
        //then
        assertEquals("해당 이메일의 유저가 없습니다.", exception.getMessage());
    }

    @Test
    @WithMockUser(username="useruser1")
    @DisplayName("입력한 비밀번호와 기존 비밀번호가 일치하여 성공한다.")
    void testCheckPassword_Success(){
        //given
        Member currentMember = memberServiceImpl.getCurrentUser().get();
        //when
        boolean result = memberServiceImpl.checkPassword(EXISTING_PASSWORD, currentMember.getId());
        //then
        assertTrue(result);
    }

    @Test
    @WithMockUser(username="useruser1")
    @DisplayName("입력한 비밀번호와 기존 비밀번호가 불일치하여 실패한다.")
    void testCheckPassword_Failure(){
        //given
        Member currentMember = memberServiceImpl.getCurrentUser().get();
        String newPassword = "newpassword";
        //when
        boolean result = memberServiceImpl.checkPassword(newPassword, currentMember.getId());
        //then
        assertFalse(result);
    }

    @Test
    @WithMockUser(username="useruser1")
    @DisplayName("현재 사용자의 아이디 변경에 성공한다.")
    void testChangeUsername_Success() {
        //given
        Member currentMember = memberServiceImpl.getCurrentUser().get();
        //when
        RsData<Member> successResult = memberServiceImpl.changeUsername(currentMember, NEW_USERNAME);
        //then
        assertThat(successResult).satisfies(result -> {
            assertThat(result.getResultCode()).isEqualTo("S-3");
            assertThat(result.getData().getUsername()).isEqualTo(NEW_USERNAME);
        });
    }

    @Test
    @WithMockUser(username="useruser1")
    @DisplayName("입력 아이디가 중복되어 아이디 변경에 실패한다.")
    void testChangeUsername_Failure() {
        //given
        Member currentMember = memberServiceImpl.getCurrentUser().get();
        //when
        RsData<Member> failureResult = memberServiceImpl.changeUsername(currentMember, EXISTING_USERNAME);
        //then
        assertThat(failureResult).satisfies(result -> {
            assertThat(result.getResultCode()).isEqualTo("F-1");
            assertThat(result.getMsg().compareTo("해당 아이디(%s)는 이미 사용중입니다.".formatted(EXISTING_USERNAME)));
        });
    }

    @Test
    @WithMockUser(username="useruser1")
    @DisplayName("현재 사용자의 비밀번호 변경에 성공한다.")
    void testChangePassword_Success() {
        //given
        Member currentMember = memberServiceImpl.getCurrentUser().get();
        //when
        RsData<Member> successResult = memberServiceImpl.changePassword(currentMember, NEW_PASSWORD);
        //then
        assertThat(successResult).satisfies(result -> {
            assertThat(result.getResultCode()).isEqualTo("S-3");
            assertTrue(passwordEncoder.matches(NEW_PASSWORD, currentMember.getPassword()));
        });
    }

    @Test
    @WithMockUser(username="useruser1")
    @DisplayName("입력 비밀번호가 현재 비밀번호와 동일하여 변경에 실패한다.")
    void testChangePassword_Failure() {
        //given
        Member currentMember = memberServiceImpl.getCurrentUser().get();
        //when
        RsData<Member> failureResult = memberServiceImpl.changePassword(currentMember, EXISTING_PASSWORD);
        //then
        assertThat(failureResult).satisfies(result -> {
            assertThat(result.getResultCode()).isEqualTo("F-1");
            assertTrue(passwordEncoder.matches(EXISTING_PASSWORD, currentMember.getPassword()));
            assertThat(result.getMsg().compareTo("비밀번호가 동일합니다."));
        });
    }
}