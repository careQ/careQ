package com.reve.careQ.domain.Chat.entity;

import com.reve.careQ.domain.Admin.entity.Admin;
import com.reve.careQ.domain.Chat.dto.ChatDto;
import com.reve.careQ.domain.Member.entity.Member;
import com.reve.careQ.domain.Message.entity.Message;
import com.reve.careQ.global.baseEntity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@Getter
@RequiredArgsConstructor
@Entity
@SuperBuilder
public class Chat extends BaseEntity {

    @Column
    private String name;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "memberId")
    private Member member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "adminId")
    private Admin admin;

    @OneToMany(mappedBy = "chat", fetch = LAZY)
    private List<Message> messageList;

    public ChatDto toResponse(LocalDateTime modifyDate) {
        return new ChatDto(
                this.getId(),
                this.getName(),
                this.getMember().getUsername(),
                this.getCreateDate(),
                modifyDate,
                this.getAdmin().getHospital().getName(),
                this.getAdmin().getSubject().getName()
        );
    }

}