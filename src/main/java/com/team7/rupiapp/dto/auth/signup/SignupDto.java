package com.team7.rupiapp.dto.auth.signup;

import com.team7.rupiapp.dto.validation.Password;
import com.team7.rupiapp.dto.validation.Unique;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SignupDto {
    @NotBlank(message = "username must not be null")
    @Unique(column = "username", message = "username already exists")
    private String username;

    @Email(message = "email not valid")
    @Unique(column = "email", message = "email already exists")
    @NotBlank(message = "email must not be null")
    private String email;

    @Password
    @NotBlank(message = "password must not be null")
    private String password;

    @NotBlank(message = "confirm_password must not be null")
    private String confirmPassword;
}
