package com.team7.rupiapp.dto.auth.refresh;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RefreshTokenDto {
    @NotBlank(message = "refresh_token must not be null")
    private String refreshToken;

    @NotBlank(message = "pin must not be null")
    @Size(min = 6, max = 6, message = "pin must be 6 characters long")
    private String pin;
}
