package com.team7.rupiapp.dto.auth.signup;

import com.team7.rupiapp.dto.validation.ValidPassword;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SetPasswordDto {
    @ValidPassword(message = "password must not be null")
    private String password;

    @NotBlank(message = "confirm_password must not be null")
    private String confirmPassword;
}
