package com.team7.rupiapp.dto.validation;

import java.lang.annotation.*;

import com.team7.rupiapp.dto.validation.validator.PhoneNumberValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Constraint(validatedBy = PhoneNumberValidator.class)
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPhoneNumber {
    String message() default "Phone number must start with a country code prefix";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
