package com.school.management.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenRefreshDto {
    @NotBlank
    private String refreshToken;
}
