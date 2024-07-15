package com.team7.rupiapp.dto.auth.signup;

import lombok.Data;

@Data
public class SignupResponseDto {
    private String username;

    private String email;

    private String accessToken;

    private String refreshToken;
}
