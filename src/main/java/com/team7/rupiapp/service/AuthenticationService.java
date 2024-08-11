package com.team7.rupiapp.service;

import java.security.Principal;

import org.springframework.http.ResponseEntity;

import com.team7.rupiapp.dto.auth.forgot.ForgotPasswordDto;
import com.team7.rupiapp.dto.auth.forgot.ForgotUsernameDto;
import com.team7.rupiapp.dto.auth.pin.SetPinDto;
import com.team7.rupiapp.dto.auth.refresh.RefreshTokenDto;
import com.team7.rupiapp.dto.auth.refresh.RefreshTokenResponseDto;
import com.team7.rupiapp.dto.auth.signin.SigninDto;
import com.team7.rupiapp.dto.auth.signin.SigninResponseDto;
import com.team7.rupiapp.dto.auth.signup.ResendVerificationDto;
import com.team7.rupiapp.dto.auth.signup.SetPasswordDto;
import com.team7.rupiapp.dto.auth.signup.SignupDto;
import com.team7.rupiapp.dto.auth.signup.SignupResponseDto;
import com.team7.rupiapp.dto.auth.verify.VerificationDto;

public interface AuthenticationService {
    public SigninResponseDto signin(SigninDto signinDto);

    public SignupResponseDto signup(SignupDto signupDto);

    public RefreshTokenResponseDto refreshToken(RefreshTokenDto refreshTokenDto);

    public ResponseEntity<Object> verify(Principal principal, VerificationDto verificationEmailDto);

    public void resendVerification(ResendVerificationDto resendVerificationDto);

    public String forgotUsername(ForgotUsernameDto forgotUsernameDto);

    public void forgotPassword(ForgotPasswordDto forgotPasswordDto);

    public void setPassword(Principal principal, SetPasswordDto setPasswordDto);

    public void setPin(Principal principal, SetPinDto setPinDto);

    public void signOut();
}
