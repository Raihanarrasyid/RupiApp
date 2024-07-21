package com.team7.rupiapp.controller;

import org.springframework.web.bind.annotation.RestController;

import com.team7.rupiapp.dto.auth.forgot.ForgotPasswordDto;
import com.team7.rupiapp.dto.auth.pin.SetPinDto;
import com.team7.rupiapp.dto.auth.refresh.RefreshTokenDto;
import com.team7.rupiapp.dto.auth.signin.SigninDto;
import com.team7.rupiapp.dto.auth.signin.SigninResponseDto;
import com.team7.rupiapp.dto.auth.signup.ResendVerificationDto;
import com.team7.rupiapp.dto.auth.signup.SetPasswordDto;
import com.team7.rupiapp.dto.auth.signup.SignupDto;
import com.team7.rupiapp.dto.auth.verify.VerificationDto;
import com.team7.rupiapp.enums.OtpType;
import com.team7.rupiapp.service.AuthenticationService;
import com.team7.rupiapp.util.ApiResponseUtil;

import jakarta.validation.Valid;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
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
            @Valid @RequestBody ResendVerificationDto resendVerificationDto) {
        authenticationService.resendVerification(resendVerificationDto);
        return ApiResponseUtil.success(HttpStatus.OK, "Resend verification success");
    }

    @PostMapping("/verify")
    public ResponseEntity<Object> verify(@Valid @RequestBody VerificationDto verificationDto,
            Principal principal) {
        if (principal == null && verificationDto.getType() != OtpType.PASSWORD_RESET) {
            throw new BadCredentialsException("Unauthorized");
        }
        return authenticationService.verify(principal, verificationDto);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Object> forgotPasswordRequest(
            @Valid @RequestBody ForgotPasswordDto forgotPasswordDto) {
        authenticationService.forgotPassword(forgotPasswordDto);
        return ApiResponseUtil.success(HttpStatus.OK, "Forgot password success");
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<Object> refreshToken(@Valid @RequestBody RefreshTokenDto refreshTokenDto) {
        return ApiResponseUtil.success(HttpStatus.OK, "Refresh token success",
                authenticationService.refreshToken(refreshTokenDto));
    }

    @PostMapping("/set-password")
    public ResponseEntity<Object> setPassword(@Valid @RequestBody SetPasswordDto setPasswordDto, Principal principal) {
        authenticationService.setPassword(principal, setPasswordDto);
        return ApiResponseUtil.success(HttpStatus.OK, "Password set success");
    }

    @PostMapping("/set-pin")
    public ResponseEntity<Object> setPin(@RequestBody SetPinDto setPinDto, Principal principal) {
        authenticationService.setPin(principal, setPinDto);
        return ApiResponseUtil.success(HttpStatus.OK, "Pin set success");
    }

    @PostMapping("/signout")
    public ResponseEntity<Object> signOut() {
        authenticationService.signOut();
        return ApiResponseUtil.success(HttpStatus.OK, "Signout success");
    }
}
