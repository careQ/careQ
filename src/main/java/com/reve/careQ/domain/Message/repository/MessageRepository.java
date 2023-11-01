package com.reve.careQ.domain.Message.repository;

import com.reve.careQ.domain.Message.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByChatId(Long chatId);

    @Query("SELECT MAX(message.createDate) FROM Message message WHERE message.chat.id = :chatId")
    Optional<LocalDateTime> findLastMessage(@Param("chatId") Long chatId);
}
