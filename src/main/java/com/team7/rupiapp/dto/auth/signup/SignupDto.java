package com.team7.rupiapp.dto.auth.signup;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SignupDto {
    @NotBlank(message = "username must not be null")
    private String username;

    @Email(message = "email not valid")
    @NotBlank(message = "email must not be null")
    private String email;

    @NotBlank(message = "password must not be null")
    private String password;

    @NotBlank(message = "confirm_password must not be null")
    private String confirmPassword;
}
