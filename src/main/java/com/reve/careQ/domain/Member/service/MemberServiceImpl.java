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
public class MemberServiceImpl implements MemberService{
    private static final int TEMP_PASSWORD_LENGTH = 6;
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
        RsData<Member> validationData = validateJoinRequest(providerTypeCode, username, email);

        if (validationData.isFail()) {
            return validationData;
        }

        String encodedPassword = encodePassword(password);
        Member member = createMember(providerTypeCode, username, encodedPassword, email);

        return RsData.of("S-1", "회원가입이 완료되었습니다.", member);
    }

    @Transactional
    public RsData<Member> getCurrentUser() {
        Authentication authentication = getAuthentication();

        RsData<Member> authenticationValidation = isAuthenticatedRs(authentication);
        if (!authenticationValidation.isSuccess()) {
            return authenticationValidation;
        }

        String username = authentication.getName();
        Member member = findByUsername(username)
                .orElseThrow(() -> new RuntimeException("현재 로그인한 사용자를 찾을 수 없습니다."));

        return RsData.of("S-3", "현재 로그인한 사용자를 가져왔습니다.", member);
    }

    public List<Reservation> getReservationsForMember(Member currentUser) {
        return reservationRepository.findByMember(currentUser);
    }

    @Transactional
    public RsData<Member> findPassword(String username, String email) {
        RsData<Member> validationData = validateFindPasswordRequest(username, email);

        if (validationData.isFail()) {
            return validationData;
        }

        return RsData.of("S-3", "입력하신 이메일로 임시 비밀번호를 전송했습니다.", validationData.getData());
    }

    @Transactional
    public void modifyPassword(String email) throws EmailException {
        String tempPassword = generateTempPassword();
        String encodedPassword = encodePassword(tempPassword);

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new EmailException("해당 이메일의 유저가 없습니다."));
        member.setPassword(encodedPassword);
        memberRepository.save(member);

        tempPasswordMail.sendSimpleMessage(email, tempPassword);
    }

    private Member createMember(String providerTypeCode, String username, String password, String email){
        Member member = Member
                .builder()
                .providerTypeCode(providerTypeCode)
                .username(username)
                .password(password)
                .email(email)
                .build();

        return memberRepository.save(member);
    }

    private String generateTempPassword() {
        return UUID.randomUUID().toString().substring(0, TEMP_PASSWORD_LENGTH);
    }

    private String encodePassword(String password){
        return StringUtils.hasText(password) ? passwordEncoder.encode(password) : null;
    }

    private RsData<Member> validateJoinRequest(String providerTypeCode, String username, String email) {
        RsData<Member> usernameValidation = isUsernameAlreadyUsedRs(username);
        if (!usernameValidation.isSuccess()) {
            return usernameValidation;
        }

        RsData<Member> emailValidation = isEmailAlreadyUsedRs(email, providerTypeCode);
        if (!emailValidation.isSuccess()) {
            return emailValidation;
        }

        return RsData.success();
    }

    private boolean isEmailAlreadyUsed(String email, String providerTypeCode) {
        return providerTypeCode.equals("careQ") && findByEmail(email).isPresent();
    }

    private RsData<Member> isUsernameAlreadyUsedRs (String username) {
        return findByUsername(username).map(member -> RsData.<Member>of("F-1", "해당 아이디(%s)는 이미 사용중입니다.".formatted(username)))
                .orElse(RsData.success());
    }

    private RsData<Member> isEmailAlreadyUsedRs (String email, String providerTypeCode) {
        return isEmailAlreadyUsed(email, providerTypeCode) ? RsData.of("F-2", "해당 이메일(%s)은 이미 사용중입니다.".formatted(email)) : RsData.success();
    }

    private Authentication getAuthentication(){
        return SecurityContextHolder.getContext().getAuthentication();
    }

    private boolean isAuthenticated(Authentication authentication){
        return authentication != null;
    }

    private RsData<Member> isAuthenticatedRs(Authentication authentication){
        return isAuthenticated(authentication) ? RsData.success() : RsData.of("F-3", "현재 로그인되어 있지 않습니다.");
    }

    private RsData<Member> findExistingMemberRs(String username, String email){
        return findByUsernameAndEmail(username, email).map(member -> RsData.of("S-1", "해당 회원이 존재합니다.", member))
                .orElse(RsData.of("F-4", "해당 계정은 존재하지 않습니다."));
    }

    private boolean isCareQProviderTypeCode(String providerTypeCode){
        return "careQ".equals(providerTypeCode);
    }

    private RsData<Member> isCareQProviderTypeCodeRs(String providerTypeCode){
        return isCareQProviderTypeCode(providerTypeCode) ? RsData.success() : RsData.of("F-4", "해당 계정은 %s와 연동된 계정입니다. %s로 로그인 해주세요".formatted(providerTypeCode, providerTypeCode));
    }

    private RsData<Member> validateFindPasswordRequest(String username, String email) {
        RsData<Member> existingMemberValidation = findExistingMemberRs(username, email);
        if (!existingMemberValidation.isSuccess()) {
            return existingMemberValidation;
        }

        RsData<Member> typeValidation = isCareQProviderTypeCodeRs(existingMemberValidation.getData().getProviderTypeCode());
        if (!typeValidation.isSuccess()) {
            return typeValidation;
        }

        return existingMemberValidation;
    }
}