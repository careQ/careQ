package com.reve.careQ.domain.Chat.service;

import com.reve.careQ.domain.Admin.entity.Admin;
import com.reve.careQ.domain.Admin.service.AdminService;
import com.reve.careQ.domain.Chat.entity.Chat;
import com.reve.careQ.domain.Chat.repository.ChatRepository;
import com.reve.careQ.domain.Member.entity.Member;
import com.reve.careQ.domain.Member.service.MemberService;
import com.reve.careQ.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatService {
    private final ChatRepository chatRepository;

    private final MemberService memberService;

    private final AdminService adminService;

    public Optional<Chat> findByMemberIdAndAdminId(Long memberId, Long hospitalId){
        return chatRepository.findByMemberIdAndAdminId(memberId, hospitalId);
    }

    public List<Chat> findByMemberId(Long memberId){
        return chatRepository.findByMemberId(memberId);
    }

    @Transactional
    public RsData<Chat> insert(Long memberId, Long adminId) {
        if (findByMemberIdAndAdminId(memberId, adminId).isPresent()) {
            return RsData.of("S-1", "채팅방이 이미 존재합니다.", findByMemberIdAndAdminId(memberId, adminId).get());
        }

        Member member= memberService.findById(memberId).get();
        Admin admin = adminService.findById(adminId).get();

        Chat chat = Chat
                .builder()
                .name(member.getUsername()+"_"+admin.getHospital().getName()+"_"+admin.getSubject().getName())
                .member(member)
                .admin(admin)
                .build();

        chatRepository.save(chat);

        return RsData.of("S-1", "새로운 채팅방이 생성되었습니다.", chat);

    }
}
