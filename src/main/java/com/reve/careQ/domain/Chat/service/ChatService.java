package com.reve.careQ.domain.Chat.service;

import com.reve.careQ.domain.Chat.entity.Chat;
import com.reve.careQ.global.rsData.RsData;

import java.util.List;
import java.util.Optional;

public interface ChatService {
    Optional<Chat> findByMemberIdAndAdminId(Long memberId, Long hospitalId);

    List<Chat> findByMemberId(Long memberId);

    List<Chat> findByAdminId(Long adminId);

    Optional<Chat> findById(Long chatId);

    List<Chat> selectChatByAdminIdAndMemberName(Long adminId, String name);

    RsData<Chat> insert(Long memberId, Long adminId);
}
