package com.reve.careQ.domain.Member.service;

import com.reve.careQ.domain.Member.entity.Member;
import com.reve.careQ.domain.Member.repository.MemberRepository;
import com.reve.careQ.domain.Reservation.entity.Reservation;
import com.reve.careQ.domain.Reservation.repository.ReservationRepository;
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

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final ReservationRepository reservationRepository;

    public Optional<Member> findById(Long id) {
        return memberRepository.findById(id);
    }

    public Optional<Member> findByUsername(String username) {
        return memberRepository.findByUsername(username);
    }

    private Optional<Member> findByEmail(String email) {
        return memberRepository.findByEmail(email);
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
    public RsData<Member> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<Member> memberOptional = findByUsername(username);

        if (memberOptional.isEmpty()) {
            return RsData.of("F-4", "현재 로그인한 사용자를 찾을 수 없습니다.");
        }

        return RsData.of("S-3", "현재 로그인한 사용자를 가져왔습니다.", memberOptional.get());
    }


    public RsData<List<Reservation>> getReservationsForCurrentUser() {
        RsData<Member> currentUserData = getCurrentUser();
        if (currentUserData.isSuccess()) {
            Member currentUser = currentUserData.getData();
            List<Reservation> reservations = reservationRepository.findByMember(currentUser);
            return RsData.of("S-3", "현재 로그인한 사용자의 예약 목록을 가져왔습니다.", reservations);
        } else {
            return RsData.of("F-4", "현재 로그인한 사용자를 찾을 수 없습니다.", null);
        }
    }

    public List<Reservation> getReservationsForMember(Member currentUser) {
        return reservationRepository.findByMember(currentUser);
    }
}