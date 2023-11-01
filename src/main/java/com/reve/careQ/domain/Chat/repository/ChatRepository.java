package com.reve.careQ.domain.Chat.repository;

import com.reve.careQ.domain.Chat.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    Optional<Chat> findByMemberIdAndAdminId(Long memberId, Long adminId);

    List<Chat> findByMemberId(Long memberId);

    List<Chat> findByAdminId(Long adminId);

    Optional<Chat> findById(Long chatId);

    @Query("SELECT chat FROM Chat chat WHERE chat.admin.id = :adminId " +
            "and chat.member.username like %:name%")
    List<Chat> selectChatByAdminIdAndMemberName(@Param("adminId") Long adminId, @Param("name") String name);
}
