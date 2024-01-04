package com.reve.careQ.domain.Message.service;

import com.reve.careQ.domain.Chat.entity.Chat;
import com.reve.careQ.domain.Message.entity.Message;
import com.reve.careQ.domain.Message.dto.MessageDto;
import com.reve.careQ.domain.Message.repository.MessageRepository;
import com.reve.careQ.global.rsData.RsData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MessageService {

    private final MessageRepository messageRepository;

    public List<Message> findByChatId(Long chatId){return messageRepository.findByChatId(chatId);}

    public Optional<LocalDateTime> findLastMessage(Long chatId){return messageRepository.findLastMessage(chatId);}

    @Transactional
    public RsData<Message> insert(MessageDto messageDto,Chat chat) {

        Message message = Message
                .builder()
                .chat(chat)
                .content(messageDto.getContent())
                .userType(messageDto.getUserType().toString())
                .build();

        messageRepository.save(message);

        return RsData.of("S-1", "메세지가 저장되었습니다.", message);
    }
}
