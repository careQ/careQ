package com.reve.careQ.domain.Message.service;

import com.reve.careQ.domain.Chat.entity.Chat;
import com.reve.careQ.domain.Message.dto.MessageDto;
import com.reve.careQ.domain.Message.entity.Message;
import com.reve.careQ.global.rsData.RsData;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MessageService {
    List<Message> findByChatId(Long chatId);

    Optional<LocalDateTime> findLastMessage(Long chatId);

    RsData<Message> insert(MessageDto messageDto, Chat chat);
}
