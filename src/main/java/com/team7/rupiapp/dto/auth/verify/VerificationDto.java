package com.team7.rupiapp.dto.auth.verify;

import com.team7.rupiapp.dto.validation.ValidEnum;
import com.team7.rupiapp.dto.validation.ValidVerificationOtp;
import com.team7.rupiapp.enums.OtpType;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@ValidVerificationOtp
public class VerificationDto {
    @ValidEnum(enumClass = OtpType.class, message = "otp type must be one of {enumValues}")
    private OtpType type;

    @NotBlank(message = "OTP is required")
    private String otp;

    private String username;
    private String password;
    private String confirmPassword;
}
