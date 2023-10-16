package com.reve.careQ.domain.Admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@AllArgsConstructor
@Getter
public class JoinFormDto {

    @NotBlank
    @Size(min = 4, max = 30)
    private final String hospitalCode;

    @NotBlank
    @Size(min = 4, max = 30)
    private final String subjectCode;

    @NotBlank
    @Size(min = 8, max = 30)
    private final String username;

    @NotBlank
    @Size(min = 4, max = 30)
    private final String password;


}