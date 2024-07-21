package com.team7.rupiapp.dto.auth.signup;

import com.team7.rupiapp.dto.validation.ValidUnique;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SignupDto {
    @NotBlank(message = "full_name must not be null")
    private String fullName;

    @NotBlank(message = "username must not be null")
    @ValidUnique(column = "username", message = "username already exists")
    private String username;

    @Email(message = "email not valid")
    @ValidUnique(column = "email", message = "email already exists")
    @NotBlank(message = "email must not be null")
    private String email;

    @ValidUnique(column = "phone", message = "phone already exists")
    @NotBlank(message = "phone must not be null")
    private String phone;

    private String password;

    private String confirmPassword;
}
