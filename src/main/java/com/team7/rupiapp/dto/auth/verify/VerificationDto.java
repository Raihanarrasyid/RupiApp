package com.team7.rupiapp.dto.auth.verify;

import com.team7.rupiapp.dto.validation.ValidEnum;
import com.team7.rupiapp.dto.validation.ValidPassword;
import com.team7.rupiapp.dto.validation.ValidVerificationOtp;
import com.team7.rupiapp.enums.VerificationType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@ValidVerificationOtp
public class VerificationDto {
    @NotNull(message = "OTP type is required")
    @ValidEnum(enumClass = VerificationType.class, message = "otp type must be one of {enumValues}")
    private VerificationType type;

    @NotBlank(message = "OTP is required")
    private String otp;

    private String username;

    @ValidPassword(nullable = true)
    private String password;

    private String confirmPassword;
}
