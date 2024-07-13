package com.team7.rupiapp.dto.auth.signin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SigninDto {
    @NotBlank(message = "username or email must not be null")
    private String username;

    @NotNull(message = "password must not be null")
    private String password;
}
