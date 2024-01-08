package com.reve.careQ.domain.Chat.service;

import com.reve.careQ.domain.Chat.entity.Chat;
import com.reve.careQ.global.rsData.RsData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ChatServiceImplTest {
    @Autowired
    private ChatServiceImpl chatServiceImpl;

    private final Long MEMBERID = (long) 1;
    private final Long EXISTING_CHAT_ADMINID = (long) 1;
    private final Long NEW_CHAT_ADMINID = (long) 2;


    @Test
    @DisplayName("채팅방이 존재하지 않을 경우 새로운 채팅방이 생성된다.")
    void testInsert_Success() {
        // 채팅방이 존재하지 않을 경우
        // given
        // when
        RsData<Chat> successResult = chatServiceImpl.insert(MEMBERID, NEW_CHAT_ADMINID);

        // then
        assertThat(successResult).satisfies(result -> {
            assertThat(result.getResultCode()).isEqualTo("S-1");
            assertThat(result.getData().getMember().getId().compareTo(MEMBERID));
            assertThat(result.getData().getAdmin().getId().compareTo(NEW_CHAT_ADMINID));
        });
    }

    @Test
    @DisplayName("채팅방이 존재할 경우 해당 채팅방으로 이동한다.")
    void testInsert_Success_2() {
        // 채팅방이 존재할 경우
        // given
        // when
        RsData<Chat> successResult = chatServiceImpl.insert(MEMBERID, EXISTING_CHAT_ADMINID);

        // then
        assertThat(successResult).satisfies(result -> {
            assertThat(result.getResultCode()).isEqualTo("S-2");
            assertThat(result.getData().getMember().getId().compareTo(MEMBERID));
            assertThat(result.getData().getAdmin().getId().compareTo(EXISTING_CHAT_ADMINID));
        });
    }
}