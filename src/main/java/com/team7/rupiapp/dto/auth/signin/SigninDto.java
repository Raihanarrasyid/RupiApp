package com.team7.rupiapp.dto.auth.signin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SigninDto {
    @NotBlank(message = "Username is required")
    private String username;

    @NotNull(message = "Password is required")
    private String password;
}
