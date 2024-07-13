package com.team7.rupiapp.service;

import org.springframework.http.ResponseEntity;

import com.team7.rupiapp.dto.auth.forgot.ForgotPasswordDto;
import com.team7.rupiapp.dto.auth.forgot.ForgotPasswordRequestDto;
import com.team7.rupiapp.dto.auth.pin.PinDto;
import com.team7.rupiapp.dto.auth.pin.SetPinDto;
import com.team7.rupiapp.dto.auth.refresh.RefreshTokenDto;
import com.team7.rupiapp.dto.auth.refresh.RefreshTokenResponseDto;
import com.team7.rupiapp.dto.auth.signin.SigninDto;
import com.team7.rupiapp.dto.auth.signin.SigninResponseDto;
import com.team7.rupiapp.dto.auth.signup.ResendVerificationEmailDto;
import com.team7.rupiapp.dto.auth.signup.SignupDto;
import com.team7.rupiapp.dto.auth.signup.SignupResponseDto;
import com.team7.rupiapp.dto.auth.signup.VerificationEmailDto;

public interface AuthenticationService {
    public SigninResponseDto signin(SigninDto signinDto);

    public SignupResponseDto signup(SignupDto signupDto);

    public void resendVerificationEmail(ResendVerificationEmailDto resendEmailDto);

    public void verifyEmail(String name, VerificationEmailDto verificationEmailDto);

    public void forgotPasswordRequest(ForgotPasswordRequestDto forgotPasswordDto);

    public void forgotPassword(String name, ForgotPasswordDto forgotPasswordDto);

    public RefreshTokenResponseDto refreshToken(RefreshTokenDto refreshTokenDto);

    public ResponseEntity<Object> setPin(String name, SetPinDto setPinDto);
}
