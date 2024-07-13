package com.team7.rupiapp.dto.auth.forgot;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ForgotPasswordDto {
    @NotBlank(message = "otp must not be null")
    private String otp;

    @NotBlank(message = "password must not be null")
    private String password;

    @NotBlank(message = "confirm_password must not be null")
    private String confirmPassword;
}
