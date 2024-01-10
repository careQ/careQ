package com.reve.careQ.domain.Member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class OnsiteRegisterDto {

    @NotBlank
    @Size(min = 8, max = 30)
    private final String username;

    @NotBlank
    private final String email;
}

