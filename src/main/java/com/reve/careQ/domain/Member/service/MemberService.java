package com.reve.careQ.domain.Member.service;

import com.reve.careQ.domain.Member.entity.Member;
import com.reve.careQ.domain.Member.repository.MemberRepository;
import com.reve.careQ.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final PasswordEncoder passwordEncoder;

    private final MemberRepository memberRepository;

    public Optional<Member> findByUsername(String username) {
        return memberRepository.findByUsername(username);
    }

    private Optional<Member> findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    @Transactional
    public RsData<Member> join(String username, String password, String email, String role) {
        return join("careQ", username, password, email, role);
    }

    public RsData<Member> join(String providerTypeCode, String username, String password, String email, String role) {
        if (findByUsername(username).isPresent()) {
            return RsData.of("F-1", "해당 아이디(%s)는 이미 사용중입니다.".formatted(username));
        }

        if (StringUtils.hasText(password)) {
            password = passwordEncoder.encode(password);
        }

        if (findByEmail(email).isPresent()) {
            return RsData.of("F-2", "해당 이메일(%s)은 이미 사용중입니다.".formatted(email));
        }

        Member member = Member
                .builder()
                .username(username)
                .password(password)
                .email(email)
                .role(role)
                .build();

        memberRepository.save(member);

        return RsData.of("S-1", "회원가입이 완료되었습니다.", member);

    }

}