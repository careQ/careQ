package com.reve.careQ.domain.Member.service;

import com.reve.careQ.domain.Member.entity.Member;
import com.reve.careQ.domain.Member.repository.MemberRepository;
import com.reve.careQ.domain.Reservation.entity.Reservation;
import com.reve.careQ.domain.Reservation.repository.ReservationRepository;
import com.reve.careQ.global.mail.EmailException;
import com.reve.careQ.global.mail.TempPasswordMail;
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
    private final ReservationRepository reservationRepository;
    private final TempPasswordMail tempPasswordMail;

    public Optional<Member> findById(Long id) {
        return memberRepository.findById(id);
    }

    public Optional<Member> findByUsername(String username) {
        return memberRepository.findByUsername(username);
    }

    private Optional<Member> findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    private Optional<Member> findByUsernameAndEmail(String username, String email) { return memberRepository.findByUsernameAndEmail(username, email); }

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
    public RsData<Member> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null){
            return RsData.of("F-3", "현재 로그인되어 있지 않습니다.");
        }

        String username = authentication.getName();
        Optional<Member> memberOptional = findByUsername(username);

        if (memberOptional.isEmpty()) {
            return RsData.of("F-4", "현재 로그인한 사용자를 찾을 수 없습니다.");
        }

        return RsData.of("S-3", "현재 로그인한 사용자를 가져왔습니다.", memberOptional.get());
    }

    public List<Reservation> getReservationsForMember(Member currentUser) {
        return reservationRepository.findByMember(currentUser);
    }

    @Transactional
    public RsData<Member> findPassword(String username, String email) {
        Optional<Member> memberOptional = findByUsernameAndEmail(username, email);

        if (memberOptional.isEmpty()) {
            return RsData.of("F-4", "해당 계정은 존재하지 않습니다.");
        }

        if (!"careQ".equals(memberOptional.get().getProviderTypeCode())){
            String type = memberOptional.get().getProviderTypeCode();
            return RsData.of("F-4", "해당 계정은 %s와 연동된 계정입니다. %s로 로그인 해주세요".formatted(type, type));
        }

        return RsData.of("S-3", "입력하신 이메일로 임시 비밀번호를 전송했습니다.", memberOptional.get());
    }

    @Transactional
    public void modifyPassword(String email) throws EmailException {
        String tempPassword = UUID.randomUUID().toString().substring(0, 6);

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new EmailException("해당 이메일의 유저가 없습니다."));
        member.setPassword(passwordEncoder.encode(tempPassword));
        memberRepository.save(member);

        tempPasswordMail.sendSimpleMessage(email, tempPassword);
    }
}