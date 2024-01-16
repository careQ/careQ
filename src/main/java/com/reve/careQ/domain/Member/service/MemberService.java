package com.reve.careQ.domain.Member.service;

import com.reve.careQ.domain.Member.entity.Member;
import com.reve.careQ.global.mail.EmailException;
import com.reve.careQ.global.rsData.RsData;

import java.util.Optional;

public interface MemberService{
    Optional<Member> findById(Long id);
    Optional<Member> findByUsername(String username);
    RsData<Member> join(String providerTypeCode, String username, String password, String email);
    Optional<Member> getCurrentUser();
    RsData<Member> findPassword(String username, String email);
    void modifyPassword(String email) throws EmailException;
    boolean checkPassword(String password, Long id);
    RsData<Member> changeUsername(Member member, String username);
    RsData<Member> changePassword(Member member, String newpassword);
    Member createMember(String providerTypeCode, String username, String password, String email);
    RsData<Member> validateJoinRequest(String providerTypeCode, String username, String email);
}
