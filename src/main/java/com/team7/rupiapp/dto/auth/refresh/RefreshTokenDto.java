package com.team7.rupiapp.dto.auth.refresh;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshTokenDto {
    @NotBlank(message = "Refresh token is required")
    private String refreshToken;

    @NotBlank(message = "PIN is required")
    private String pin;
}
