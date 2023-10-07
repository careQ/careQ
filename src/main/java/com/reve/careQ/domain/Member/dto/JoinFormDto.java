package com.reve.careQ.domain.Member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@AllArgsConstructor
@Getter
public class JoinFormDto {

    @NotBlank
    @Size(min = 4, max = 30)
    private final String username;

    @NotBlank
    @Size(min = 4, max = 30)
    private final String password;

    @NotBlank
    private String email;

}
