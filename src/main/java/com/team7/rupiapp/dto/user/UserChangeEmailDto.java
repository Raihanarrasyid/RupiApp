package com.team7.rupiapp.dto.user;

import com.team7.rupiapp.dto.validation.ValidUnique;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserChangeEmailDto {
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @ValidUnique(column = "email", message = "Email already been taken")
    private String email;

    public void setEmail(String email) {
        this.email = email != null ? email.toLowerCase() : null;
    }
}
