package com.team7.rupiapp.dto.validation.validator;

import com.team7.rupiapp.dto.auth.verify.VerificationDto;
import com.team7.rupiapp.dto.validation.ValidEnum;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EnumValidator implements ConstraintValidator<ValidEnum, Enum<?>> {
    private Class<? extends Enum<?>> enumClass;

    @Override
    public void initialize(ValidEnum annotation) {
        this.enumClass = annotation.enumClass();
    }

    @Override
    public boolean isValid(Enum<?> value, ConstraintValidatorContext context) {
        if (value == null) {
            String validValues = getEnumValuesAsString(this.enumClass.getEnumConstants());
            String message = context.getDefaultConstraintMessageTemplate().replace("{enumValues}", validValues);

            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message)
                    .addConstraintViolation();

            return false;
        }

        Object[] enumValues = this.enumClass.getEnumConstants();
        for (Object enumValue : enumValues) {
            if (value.equals(enumValue)) {
                return true;
            }
        }

        return false;
    }

    private String getEnumValuesAsString(Object[] enumValues) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < enumValues.length; i++) {
            sb.append(enumValues[i]);
            if (i < enumValues.length - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }
}