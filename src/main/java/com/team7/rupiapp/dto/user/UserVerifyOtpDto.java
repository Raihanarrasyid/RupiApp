package com.team7.rupiapp.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserVerifyOtpDto {
    @NotBlank(message = "OTP is required")
    private String otp;
}
