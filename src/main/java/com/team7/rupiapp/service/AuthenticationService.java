package com.team7.rupiapp.service;

import java.security.Principal;

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
import com.team7.rupiapp.dto.auth.verify.VerificationDto;

public interface AuthenticationService {
    public SigninResponseDto signin(SigninDto signinDto);

    public SignupResponseDto signup(SignupDto signupDto);

    public void resendVerificationEmail(ResendVerificationEmailDto resendEmailDto);

    public ResponseEntity<Object> verify(Principal principal, VerificationDto verificationEmailDto);

    public void forgotPassword(ForgotPasswordRequestDto forgotPasswordDto);

    public RefreshTokenResponseDto refreshToken(RefreshTokenDto refreshTokenDto);

    public void setPin(String name, SetPinDto setPinDto);

    public void signOut(Principal principal);
}
