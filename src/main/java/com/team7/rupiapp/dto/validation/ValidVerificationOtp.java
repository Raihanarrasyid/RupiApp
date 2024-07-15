package com.team7.rupiapp.dto.validation;

import java.lang.annotation.*;

import com.team7.rupiapp.dto.validation.validator.VerificationOtpValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = VerificationOtpValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidVerificationOtp {
    String message() default "Invalid verification request";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
