package com.team7.rupiapp.dto.auth.signup;

import com.team7.rupiapp.dto.validation.ValidPassword;
import com.team7.rupiapp.dto.validation.ValidUnique;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SignupDto {
    @NotBlank(message = "username must not be null")
    @ValidUnique(column = "username", message = "username already exists")
    private String username;

    @Email(message = "email not valid")
    @ValidUnique(column = "email", message = "email already exists")
    @NotBlank(message = "email must not be null")
    private String email;

    @ValidPassword(message = "password must not be null")
    private String password;

    @NotBlank(message = "confirm_password must not be null")
    private String confirmPassword;
}
