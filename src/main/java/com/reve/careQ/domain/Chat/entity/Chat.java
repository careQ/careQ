package com.reve.careQ.domain.Chat.entity;

import com.reve.careQ.domain.Admin.entity.Admin;
import com.reve.careQ.domain.Member.entity.Member;
import com.reve.careQ.domain.Message.entity.Message;
import com.reve.careQ.global.baseEntity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@Getter
@RequiredArgsConstructor
@Entity
@SuperBuilder
public class Chat extends BaseEntity {

    @Column
    private String chat_name;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "admin_id")
    private Admin admin;

    @OneToMany(mappedBy = "chat", fetch = LAZY)
    private List<Message> messageList;

}