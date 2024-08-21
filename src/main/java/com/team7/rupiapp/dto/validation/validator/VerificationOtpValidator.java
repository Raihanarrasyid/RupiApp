package com.team7.rupiapp.dto.validation.validator;

import com.team7.rupiapp.dto.auth.verify.VerificationDto;
import com.team7.rupiapp.dto.validation.ValidVerificationOtp;
import com.team7.rupiapp.enums.VerificationType;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ConstraintViolation;

import java.util.Set;

public class VerificationOtpValidator implements ConstraintValidator<ValidVerificationOtp, VerificationDto> {

    private static final String USERNAME_FIELD = "username";
    private static final String PASSWORD_FIELD = "password";
    private static final String CONFIRM_PASSWORD_FIELD = "confirmPassword";

    private static final String USERNAME_REQUIRED_MSG = "Username is required when type is FORGOT_PASSWORD";
    private static final String PASSWORD_REQUIRED_MSG = "Password is required when type is FORGOT_PASSWORD";
    private static final String CONFIRM_PASSWORD_REQUIRED_MSG = "Confirm password is required when type is FORGOT_PASSWORD";

    private Validator validator;

    @Override
    public void initialize(ValidVerificationOtp constraintAnnotation) {
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Override
    public boolean isValid(VerificationDto dto, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();

        if (dto.getType() == VerificationType.FORGOT_PASSWORD) {
            return validateForgotPassword(dto, context);
        }

        return true;
    }

    private boolean validateForgotPassword(VerificationDto dto, ConstraintValidatorContext context) {
        boolean isValid = true;

        if (dto.getUsername() == null || dto.getUsername().isBlank()) {
            context.buildConstraintViolationWithTemplate(USERNAME_REQUIRED_MSG)
                    .addPropertyNode(USERNAME_FIELD)
                    .addConstraintViolation();
            isValid = false;
        }

        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            context.buildConstraintViolationWithTemplate(PASSWORD_REQUIRED_MSG)
                    .addPropertyNode(PASSWORD_FIELD)
                    .addConstraintViolation();
            isValid = false;
        } else {
            Set<ConstraintViolation<VerificationDto>> violations = validator.validateProperty(dto, PASSWORD_FIELD);

            if (!violations.isEmpty()) {
                for (ConstraintViolation<VerificationDto> violation : violations) {
                    context.buildConstraintViolationWithTemplate(violation.getMessage())
                            .addPropertyNode(PASSWORD_FIELD)
                            .addConstraintViolation();
                }
                isValid = false;
            }
        }

        if (dto.getConfirmPassword() == null || dto.getConfirmPassword().isBlank()) {
            context.buildConstraintViolationWithTemplate(CONFIRM_PASSWORD_REQUIRED_MSG)
                    .addPropertyNode(CONFIRM_PASSWORD_FIELD)
                    .addConstraintViolation();
            isValid = false;
        }

        return isValid;
    }
}
