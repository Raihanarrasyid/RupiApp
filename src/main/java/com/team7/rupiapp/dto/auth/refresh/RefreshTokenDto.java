package com.team7.rupiapp.dto.auth.refresh;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshTokenDto {
    @NotBlank(message = "refresh_token must not be null")
    private String refreshToken;

    @NotBlank(message = "pin must not be null")
    private String pin;
}
