package com.reve.careQ.domain.Member.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.reve.careQ.domain.Member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDto {

    @JsonProperty("member_id")
    private Long id;

    @JsonProperty("username")
    private String username;

    @JsonProperty("created_at")
    private LocalDateTime createDate;

    @JsonProperty("updated_at")
    private LocalDateTime modifyDate;

    public static MemberDto fromUser(Member member) {
        MemberDto memberDto = MemberDto.builder()
                .id(member.getId())
                .username(member.getUsername())
                .createDate(member.getCreateDate())
                .modifyDate(member.getModifyDate())
                .build();

        return memberDto;
    }
}