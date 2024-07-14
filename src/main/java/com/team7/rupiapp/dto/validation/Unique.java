package com.team7.rupiapp.dto.validation;

import java.lang.annotation.*;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = UniqueValidator.class)
public @interface Unique {
    String message() default "Field already exists";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    String column();
}
