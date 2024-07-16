package com.team7.rupiapp.controller;

import org.springframework.web.bind.annotation.RestController;

import com.team7.rupiapp.dto.auth.forgot.ForgotPasswordDto;
import com.team7.rupiapp.dto.auth.pin.SetPinDto;
import com.team7.rupiapp.dto.auth.refresh.RefreshTokenDto;
import com.team7.rupiapp.dto.auth.signin.SigninDto;
import com.team7.rupiapp.dto.auth.signin.SigninResponseDto;
import com.team7.rupiapp.dto.auth.signup.ResendVerificationEmailDto;
import com.team7.rupiapp.dto.auth.signup.SignupDto;
import com.team7.rupiapp.dto.auth.verify.VerificationDto;
import com.team7.rupiapp.service.AuthenticationService;
import com.team7.rupiapp.util.ApiResponseUtil;

import jakarta.validation.Valid;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationService authenticationService;

    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signin")
    public ResponseEntity<Object> signin(@Valid @RequestBody SigninDto signinDto) {
        SigninResponseDto response = authenticationService.signin(signinDto);
        return ApiResponseUtil.success(HttpStatus.OK, "Signin success", response);
    }

    @PostMapping("/signup")
    public ResponseEntity<Object> signup(@Valid @RequestBody SignupDto signupDto) {
        return ApiResponseUtil.success(HttpStatus.OK, "Signup success", authenticationService.signup(signupDto));
    }

    @PostMapping("/verify/resend")
    public ResponseEntity<Object> resendVerificationEmail(
            @Valid @RequestBody ResendVerificationEmailDto resendEmailDto) {
        authenticationService.resendVerificationEmail(resendEmailDto);
        return ApiResponseUtil.success(HttpStatus.OK, "Verification email sent");
    }

    @PostMapping("/verify")
    public ResponseEntity<Object> verify(@Valid @RequestBody VerificationDto verificationEmailDto,
            Principal principal) {
        return authenticationService.verify(principal, verificationEmailDto);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Object> forgotPasswordRequest(
            @Valid @RequestBody ForgotPasswordDto forgotPasswordRequestDto) {
        authenticationService.forgotPassword(forgotPasswordRequestDto);
        return ApiResponseUtil.success(HttpStatus.OK, "Forgot password success");
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<Object> refreshToken(@Valid @RequestBody RefreshTokenDto refreshTokenDto) {
        return ApiResponseUtil.success(HttpStatus.OK, "Refresh token success",
                authenticationService.refreshToken(refreshTokenDto));
    }

    @PostMapping("/set-pin")
    public ResponseEntity<Object> setPin(@RequestBody SetPinDto setPinDto, Principal principal) {
        authenticationService.setPin(principal.getName(), setPinDto);
        return ApiResponseUtil.success(HttpStatus.OK, "Pin set success");
    }

    @PostMapping("/signout")
    public ResponseEntity<Object> signOut(Principal principal) {
        authenticationService.signOut(principal);
        return ApiResponseUtil.success(HttpStatus.OK, "Signout success");
    }
}
