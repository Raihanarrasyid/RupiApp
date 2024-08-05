package com.team7.rupiapp.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserVerifyPasswordDto {
    @NotBlank(message = "Password is required")
    private String password;
}
