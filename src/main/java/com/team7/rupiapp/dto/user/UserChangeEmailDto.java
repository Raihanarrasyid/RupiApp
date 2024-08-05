package com.team7.rupiapp.dto.user;

import com.team7.rupiapp.dto.validation.ValidUnique;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserChangeEmailDto {
    @NotBlank(message = "Email is required")
    @Email(message = "email must be a valid email")
    @ValidUnique(column = "email", message = "Email already been taken")
    private String email;
}