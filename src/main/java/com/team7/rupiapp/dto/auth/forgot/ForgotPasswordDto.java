package com.team7.rupiapp.dto.auth.forgot;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ForgotPasswordDto {
    @NotBlank(message = "username or email must not be null")
    private String username;
}
