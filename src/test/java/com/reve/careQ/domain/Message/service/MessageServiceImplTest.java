package com.reve.careQ.domain.Message.service;

import com.reve.careQ.domain.Chat.entity.Chat;
import com.reve.careQ.domain.Chat.service.ChatServiceImpl;
import com.reve.careQ.domain.Message.dto.MessageDto;
import com.reve.careQ.domain.Message.entity.Message;
import com.reve.careQ.global.rsData.RsData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MessageServiceImplTest {
    @Autowired
    private MessageServiceImpl messageServiceImpl;

    @Autowired
    private ChatServiceImpl chatServiceImpl;

    private final Long EXISTING_CHATID = (long) 1;

    @Test
    @DisplayName("새로운 메세지가 성공적으로 저장된다.")
    void testInsert_Success() {
        // given
        MessageDto messageDto = new MessageDto();
        messageDto.setType(MessageDto.Type.TALK);
        messageDto.setUserType(MessageDto.UserType.MEMBER);
        messageDto.setChatId(EXISTING_CHATID);
        messageDto.setContent("메세지 내용");
        messageDto.setSender("useruser1");

        Chat existingChat = chatServiceImpl.findById(messageDto.getChatId()).get();

        // when
        RsData<Message> successResult = messageServiceImpl.insert(messageDto, existingChat);

        // then
        assertThat(successResult).satisfies(result -> {
            assertThat(result.getResultCode()).isEqualTo("S-1");
            assertThat(result.getData().getContent().compareTo("메세지 내용"));
        });
    }
}