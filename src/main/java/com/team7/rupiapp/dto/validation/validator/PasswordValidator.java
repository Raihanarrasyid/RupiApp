package com.team7.rupiapp.dto.validation.validator;

import com.team7.rupiapp.dto.validation.ValidPassword;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {
    private int minLength;
    private int minUpperCase;
    private int minLowerCase;
    private int minDigits;
    private int minSpecialChars;
    private boolean nullable;

    @Override
    public void initialize(ValidPassword constraintAnnotation) {
        this.minLength = constraintAnnotation.minLength();
        this.minUpperCase = constraintAnnotation.minUpperCase();
        this.minLowerCase = constraintAnnotation.minLowerCase();
        this.minDigits = constraintAnnotation.minDigits();
        this.minSpecialChars = constraintAnnotation.minSpecialChars();
        this.nullable = constraintAnnotation.nullable();
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
            return nullable;
        }

        StringBuilder errorMessage = new StringBuilder();

        if (password.length() < minLength) {
            errorMessage.append("Password must be at least ").append(minLength).append(" characters long. ");
        }

        int upperCount = countUpperCase(password);
        int lowerCount = countLowerCase(password);
        int digitCount = countDigits(password);
        int specialCount = countSpecialChars(password);

        if (upperCount < minUpperCase) {
            errorMessage.append("uppercase letters, ");
        }
        if (lowerCount < minLowerCase) {
            errorMessage.append("lowercase letters, ");
        }
        if (digitCount < minDigits) {
            errorMessage.append("digits, ");
        }
        if (specialCount < minSpecialChars) {
            errorMessage.append("special characters, ");
        }

        if (errorMessage.length() > 0) {
            errorMessage.delete(errorMessage.length() - 2, errorMessage.length());
            errorMessage.append(". ");
        }

        if (errorMessage.length() > 0) {
            errorMessage.insert(0, "Password must contain at least ");
            addConstraintViolation(context, errorMessage.toString().trim());
            return false;
        }

        return true;
    }

    private int countUpperCase(String password) {
        int count = 0;
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                count++;
            }
        }
        return count;
    }

    private int countLowerCase(String password) {
        int count = 0;
        for (char c : password.toCharArray()) {
            if (Character.isLowerCase(c)) {
                count++;
            }
        }
        return count;
    }

    private int countDigits(String password) {
        int count = 0;
        for (char c : password.toCharArray()) {
            if (Character.isDigit(c)) {
                count++;
            }
        }
        return count;
    }

    private int countSpecialChars(String password) {
        int count = 0;
        for (char c : password.toCharArray()) {
            if (!Character.isLetterOrDigit(c)) {
                count++;
            }
        }
        return count;
    }

    private void addConstraintViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
    }
}
