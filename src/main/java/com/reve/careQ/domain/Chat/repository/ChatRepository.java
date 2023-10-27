package com.reve.careQ.domain.Chat.repository;

import com.reve.careQ.domain.Chat.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    Optional<Chat> findByMemberIdAndAdminId(Long memberId, Long adminId);

    List<Chat> findByMemberId(Long memberId);
}
