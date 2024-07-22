package com.team7.rupiapp.dto.auth.signin;

import lombok.Data;

@Data
public class SigninResponseDto {
    private String username;

    private String email;

    private String phone;

    private String accessToken;

    private String refreshToken;
}
