package com.team7.rupiapp.dto.auth.signup;

import com.team7.rupiapp.dto.validation.ValidPassword;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SetPasswordDto {
    @ValidPassword(message = "Password is required")
    private String password;

    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;
}
