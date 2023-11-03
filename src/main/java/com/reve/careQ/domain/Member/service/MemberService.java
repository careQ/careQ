package com.reve.careQ.domain.Member.service;

import com.reve.careQ.domain.Member.entity.Member;
import com.reve.careQ.domain.Member.repository.MemberRepository;
import com.reve.careQ.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final PasswordEncoder passwordEncoder;

    private final MemberRepository memberRepository;

    public Optional<Member> findById(Long id) {
        return memberRepository.findById(id);
    }

    public Optional<Member> findByUsername(String username) {
        return memberRepository.findByUsername(username);
    }

    private List<Member> findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    private Optional<Member> findByEmailAndProviderTypeCode(String providerTypeCode, String email) {
        return memberRepository.findByEmailAndProviderTypeCode(providerTypeCode, email);
    }

    @Transactional
    public RsData<Member> join(String providerTypeCode, String username, String password, String email) {
        if (findByUsername(username).isPresent()) {
            return RsData.of("F-1", "해당 아이디(%s)는 이미 사용중입니다.".formatted(username));
        }

        if (StringUtils.hasText(password)) {
            password = passwordEncoder.encode(password);
        }

        if ((!findByEmail(email).isEmpty()) && (providerTypeCode.equals("careQ"))){
            return RsData.of("F-2", "해당 이메일(%s)은 이미 사용중입니다.".formatted(email));
        }

        Member member = Member
                .builder()
                .providerTypeCode(providerTypeCode)
                .username(username)
                .password(password)
                .email(email)
                .build();

        memberRepository.save(member);

        return RsData.of("S-1", "회원가입이 완료되었습니다.", member);

    }

    @Transactional
    public RsData<Member> whenSocialLogin(String providerTypeCode, String username, String email) {

        Optional<Member> socialMember = findByEmailAndProviderTypeCode(email, providerTypeCode);

        if (socialMember.isPresent()) return RsData.of("S-2", "로그인 되었습니다.", socialMember.get());

        String password = UUID.randomUUID().toString().substring(0, 6);

        return join(providerTypeCode, username, password, email);
    }

    @Transactional
    public RsData<Member> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<Member> memberOptional = findByUsername(username);

        if (memberOptional.isEmpty()) {
            return RsData.of("F-4", "현재 로그인한 사용자를 찾을 수 없습니다.");
        }

        return RsData.of("S-3", "현재 로그인한 사용자를 가져왔습니다.", memberOptional.get());
    }

}