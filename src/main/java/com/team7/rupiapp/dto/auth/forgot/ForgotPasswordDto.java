package com.team7.rupiapp.dto.auth.forgot;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ForgotPasswordDto {
    @NotBlank(message = "Username is required")
    private String username;

    public void setUsername(String username) {
        this.username = username != null ? username.toLowerCase() : null;
    }
}
