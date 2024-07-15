package com.team7.rupiapp.dto.validation.validator;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.team7.rupiapp.dto.validation.ValidUnique;
import com.team7.rupiapp.model.User;
import com.team7.rupiapp.repository.UserRepository;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Component
public class UniqueValidator implements ConstraintValidator<ValidUnique, String> {
    private String column;
    private final UserRepository userRepository;

    public UniqueValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void initialize(ValidUnique constraintAnnotation) {
        this.column = constraintAnnotation.column();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        Optional<User> user = Optional.empty();
        if (column.equals("username")) {
            user = userRepository.findByUsername(value);
        } else if (column.equals("email")) {
            user = userRepository.findByEmail(value);
        }
        return user.isEmpty();
    }
}
