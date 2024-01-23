package com.reve.careQ.domain.Message.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageDto {
    public enum Type{
    ENTER, TALK
    }

    public enum UserType{
        MEMBER, ADMIN
    }

    private Type type;
    private UserType userType;
    private Long chatId;
    private String sender;
    private String content;
}
