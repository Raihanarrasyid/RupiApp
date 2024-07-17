package com.team7.rupiapp.dto.auth.signup;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SignupResponseDto {
    private String username;

    private String email;

    private String accessToken;

    private String refreshToken;

    private String password;
}
