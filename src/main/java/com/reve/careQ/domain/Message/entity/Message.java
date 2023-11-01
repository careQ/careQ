package com.reve.careQ.domain.Message.entity;

import com.reve.careQ.domain.Chat.entity.Chat;
import com.reve.careQ.global.baseEntity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

import static jakarta.persistence.FetchType.LAZY;

@Getter
@RequiredArgsConstructor
@Entity
@SuperBuilder
public class Message extends BaseEntity {

    @Column
    private String content;

    @Column
    private String userType;

    @Column
    @CreationTimestamp
    private Timestamp sendTime;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "chatId")
    private Chat chat;

}