package com.reve.careQ.domain.Chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatDto {
    private Long id;

    private String name;

    private String memberUsername;

    private LocalDateTime createDate;

    private LocalDateTime modifyDate;

    private String hospitalName;

    private String subjectName;
}
