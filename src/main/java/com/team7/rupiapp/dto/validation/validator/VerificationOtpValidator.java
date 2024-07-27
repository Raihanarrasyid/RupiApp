package com.team7.rupiapp.dto.validation.validator;

import com.team7.rupiapp.dto.auth.verify.VerificationDto;
import com.team7.rupiapp.dto.validation.ValidVerificationOtp;
import com.team7.rupiapp.enums.OtpType;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class VerificationOtpValidator implements ConstraintValidator<ValidVerificationOtp, VerificationDto> {
    @Override
    public boolean isValid(VerificationDto dto, ConstraintValidatorContext context) {
        boolean isValid = true;

        if (dto.getType() == OtpType.FORGOT_PASSWORD) {
            isValid = dto.getUsername() != null && !dto.getUsername().isBlank() &&
                    dto.getPassword() != null && !dto.getPassword().isBlank() &&
                    dto.getConfirmPassword() != null && !dto.getConfirmPassword().isBlank();
            if (!isValid) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                        "username, password and confirm_password must not be null when otp_type is FORGOT_PASSWORD")
                        .addConstraintViolation();
            }
        }

        return isValid;
    }
}
