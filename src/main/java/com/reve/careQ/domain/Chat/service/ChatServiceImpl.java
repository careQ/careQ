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
public class ChatServiceImpl implements ChatService{
    private final ChatRepository chatRepository;

    private final MemberService memberService;

    private final AdminService adminService;

    public Optional<Chat> findByMemberIdAndAdminId(Long memberId, Long hospitalId){
        return chatRepository.findByMemberIdAndAdminId(memberId, hospitalId);
    }

    public List<Chat> findByMemberId(Long memberId){
        return chatRepository.findByMemberId(memberId);
    }

    public List<Chat> findByAdminId(Long adminId){
        return chatRepository.findByAdminId(adminId);
    }

    public Optional<Chat> findById(Long chatId){
        return chatRepository.findById(chatId);
    }

    public List<Chat> selectChatByAdminIdAndMemberName(Long adminId, String name){
        return chatRepository.selectChatByAdminIdAndMemberName(adminId, name);
    }

    @Transactional
    public RsData<Chat> insert(Long memberId, Long adminId) {
        RsData<Chat> validationData = isChatAlreadyExistRs(memberId, adminId);

        if (validationData.isSuccess()) {
            return validationData;
        }

        Member member= memberService.findById(memberId).get();
        Admin admin = adminService.findById(adminId).get();


        Chat chat = createChat(createChatName(member, admin), member, admin);

        return RsData.of("S-1", "새로운 채팅방이 생성되었습니다.", chat);
    }

    private String createChatName(Member member, Admin admin){
        return member.getUsername()+"_"+admin.getHospital().getName()+"_"+admin.getSubject().getName();
    }

    private Chat createChat(String name, Member member, Admin admin){
        Chat chat = Chat
                .builder()
                .name(name)
                .member(member)
                .admin(admin)
                .build();

        return chatRepository.save(chat);
    }

    private RsData<Chat> isChatAlreadyExistRs (Long memberId, Long adminId) {
        return findByMemberIdAndAdminId(memberId, adminId).map(chat -> RsData.of("S-2", "채팅방이 이미 존재합니다.", chat))
                .orElse(RsData.failOf("F-1","채팅방이 존재하지 않습니다."));
    }
}