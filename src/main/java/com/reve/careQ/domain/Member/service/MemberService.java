package com.reve.careQ.domain.Member.service;

import com.reve.careQ.domain.Member.dto.MemberQueueInfoDto;
import com.reve.careQ.domain.Member.entity.Member;
import com.reve.careQ.domain.Reservation.entity.Reservation;
import com.reve.careQ.global.mail.EmailException;
import com.reve.careQ.global.rsData.RsData;

import java.util.List;
import java.util.Optional;

public interface MemberService{
    Optional<Member> findById(Long id);
    Optional<Member> findByUsername(String username);
    RsData<Member> join(String providerTypeCode, String username, String password, String email);
    Optional<Member> getCurrentUser();
    List<Reservation> getReservationsForMember(Member currentUser);
    RsData<Member> findPassword(String username, String email);
    void modifyPassword(String email) throws EmailException;
    Member createMember(String providerTypeCode, String username, String password, String email);
    RsData<Member> validateJoinRequest(String providerTypeCode, String username, String email);
    MemberQueueInfoDto getMemberQueueInfoData(Long id);
}
