package com.reve.careQ.domain.Member.service;

import com.reve.careQ.domain.Member.entity.Member;
import com.reve.careQ.domain.Member.repository.MemberRepository;
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

import java.util.Optional;
import java.util.UUID;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService{
    private static final int TEMP_PASSWORD_LENGTH = 6;
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
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
    public Optional<Member> getCurrentUser() {
        Authentication authentication = getAuthentication();

        if (authentication == null) {
            throw new RuntimeException("인증되지 않은 사용자입니다.");
        }

        String username = authentication.getName();
        return findByUsername(username);
    }

    @Override
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

    @Override
    public Member createMember(String providerTypeCode, String username, String password, String email){
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

    public RsData<Member> validateJoinRequest(String providerTypeCode, String username, String email) {
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

    public boolean checkPassword(String password, Long id){
        return passwordEncoder.matches(password, findById(id).get().getPassword());
    }

    @Transactional
    public RsData<Member> changeUsername(Member member, String username){
        RsData<Member> usernameValidation = isUsernameAlreadyUsedRs(username);
        if (!usernameValidation.isSuccess()) {
            return usernameValidation;
        }

        Member changeMember = setUsername(member, username);

        return RsData.of("S-3", "아이디가 변경되었습니다.", changeMember);
    }

    private Member setUsername(Member member, String username){
        member.setUsername(username);
        return memberRepository.save(member);
    }

    @Transactional
    public RsData<Member> changePassword(Member member, String newpassword){
        if(passwordEncoder.matches(newpassword, member.getPassword())){
            return RsData.failOf("F-1", "비밀번호가 동일합니다.");
        }

        Member changeMember = setPassword(member, newpassword);

        return RsData.of("S-3", "비밀번호가 변경되었습니다.", changeMember);
    }

    private Member setPassword(Member member, String newpassword){
        member.setPassword(encodePassword(newpassword));
        return memberRepository.save(member);
    }
}