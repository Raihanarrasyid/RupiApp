package com.team7.rupiapp.controller;

import org.springframework.web.bind.annotation.RestController;

import com.team7.rupiapp.dto.auth.forgot.ForgotPasswordDto;
import com.team7.rupiapp.dto.auth.forgot.ForgotPasswordRequestDto;
import com.team7.rupiapp.dto.auth.pin.SetPinDto;
import com.team7.rupiapp.dto.auth.refresh.RefreshTokenDto;
import com.team7.rupiapp.dto.auth.signin.SigninDto;
import com.team7.rupiapp.dto.auth.signin.SigninResponseDto;
import com.team7.rupiapp.dto.auth.signup.ResendVerificationEmailDto;
import com.team7.rupiapp.dto.auth.signup.SignupDto;
import com.team7.rupiapp.dto.auth.signup.VerificationEmailDto;
import com.team7.rupiapp.service.AuthenticationService;
import com.team7.rupiapp.util.ApiResponseUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @PostMapping(value = "/signin")
    public ResponseEntity<Object> signin(@Valid @RequestBody SigninDto signinDto) {
        SigninResponseDto response = authenticationService.signin(signinDto);
        return ApiResponseUtil.success(HttpStatus.OK, "Signin success", response);
    }

    @PostMapping(value = "/signup")
    public ResponseEntity<Object> signup(@Valid @RequestBody SignupDto signupDto) {
        return ApiResponseUtil.success(HttpStatus.OK, "Signup success", authenticationService.signup(signupDto));
    }

    @PostMapping("/verify/resend")
    public ResponseEntity<Object> resendVerificationEmail(@Valid @RequestBody ResendVerificationEmailDto resendEmailDto) {
        authenticationService.resendVerificationEmail(resendEmailDto);
        return ApiResponseUtil.success(HttpStatus.OK, "Verification email sent");
    }

    @PostMapping("/verify")
    public ResponseEntity<Object> verifyEmail(@Valid @RequestBody VerificationEmailDto verificationEmailDto, Principal principal) {
        authenticationService.verifyEmail(principal.getName(), verificationEmailDto);
        return ApiResponseUtil.success(HttpStatus.OK, "Email verified");
    }

    @PostMapping("/forgot-password/request")
    public ResponseEntity<Object> forgotPasswordRequest(@Valid @RequestBody ForgotPasswordRequestDto forgotPasswordRequestDto) {
        authenticationService.forgotPasswordRequest(forgotPasswordRequestDto);
        return ApiResponseUtil.success(HttpStatus.OK, "Forgot password success");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Object> forgotPassword(@Valid @RequestBody ForgotPasswordDto forgotPasswordDto, Principal principal) {
        authenticationService.forgotPassword(principal.getName(), forgotPasswordDto);
        return ApiResponseUtil.success(HttpStatus.OK, "Forgot password success");
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<Object> refreshToken(@Valid @RequestBody RefreshTokenDto refreshTokenDto) {
        return ApiResponseUtil.success(HttpStatus.OK, "Refresh token success", authenticationService.refreshToken(refreshTokenDto));
    }

    @PostMapping("/set-pin")
    public ResponseEntity<Object> setPin(@RequestBody SetPinDto setPinDto, Principal principal) {
        authenticationService.setPin(principal.getName(), setPinDto);
        return ApiResponseUtil.success(HttpStatus.OK, "Pin set success");
    }
}
