package com.reve.careQ.domain.Message.controller;

import com.reve.careQ.domain.Chat.service.ChatService;
import com.reve.careQ.domain.Message.entity.MessageDto;
import com.reve.careQ.domain.Message.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    private final ChatService chatService;

    private final SimpMessageSendingOperations sendingOperations;

    @MessageMapping("/message")
    public void send(MessageDto messageDto) throws Exception {
        Thread.sleep(1000); // simulated delay

        messageService.insert(messageDto, chatService.findById(messageDto.getChatId()).get());

        sendingOperations.convertAndSend("/topic/chatrooms/"+messageDto.getChatId(),messageDto);
    }
}