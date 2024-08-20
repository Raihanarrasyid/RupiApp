package com.team7.rupiapp.dto.validation.validator;

import com.team7.rupiapp.dto.auth.verify.VerificationDto;
import com.team7.rupiapp.dto.validation.ValidVerificationOtp;
import com.team7.rupiapp.enums.VerificationType;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class VerificationOtpValidator implements ConstraintValidator<ValidVerificationOtp, VerificationDto> {

    @Override
    public boolean isValid(VerificationDto dto, ConstraintValidatorContext context) {
        boolean isValid = true;

        if (dto.getType() == VerificationType.FORGOT_PASSWORD) {
            boolean isUsernameValid = dto.getUsername() != null && !dto.getUsername().isBlank();
            boolean isPasswordValid = dto.getPassword() != null && !dto.getPassword().isBlank();
            boolean isConfirmPasswordValid = dto.getConfirmPassword() != null && !dto.getConfirmPassword().isBlank();

            if (!isUsernameValid || !isPasswordValid || !isConfirmPasswordValid) {
                isValid = false;
                context.disableDefaultConstraintViolation();

                if (!isUsernameValid) {
                    context.buildConstraintViolationWithTemplate(
                            "Username is required when type is FORGOT_PASSWORD")
                            .addPropertyNode("username")
                            .addConstraintViolation();
                }
                if (!isPasswordValid) {
                    context.buildConstraintViolationWithTemplate(
                            "Password is required when type is FORGOT_PASSWORD")
                            .addPropertyNode("password")
                            .addConstraintViolation();
                }
                if (!isConfirmPasswordValid) {
                    context.buildConstraintViolationWithTemplate(
                            "Confirm password is required when type is FORGOT_PASSWORD")
                            .addPropertyNode("confirmPassword")
                            .addConstraintViolation();
                }
            }
        }

        return isValid;
    }
}
