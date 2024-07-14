package com.team7.rupiapp.dto.validation;

import java.lang.annotation.*;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordConstraintsValidator.class)
public @interface Password {
    String message() default "Invalid password";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    int minLength() default 8;
    int minUpperCase() default 1;
    int minLowerCase() default 1;
    int minDigits() default 1;
    int minSpecialChars() default 1;
}