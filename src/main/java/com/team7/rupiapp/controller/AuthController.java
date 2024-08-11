package com.team7.rupiapp.controller;

import org.springframework.web.bind.annotation.RestController;

import com.team7.rupiapp.api.AuthApi;
import com.team7.rupiapp.dto.auth.forgot.ForgotPasswordDto;
import com.team7.rupiapp.dto.auth.forgot.ForgotUsernameDto;
import com.team7.rupiapp.dto.auth.pin.SetPinDto;
import com.team7.rupiapp.dto.auth.refresh.RefreshTokenDto;
import com.team7.rupiapp.dto.auth.signin.SigninDto;
import com.team7.rupiapp.dto.auth.signin.SigninResponseDto;
import com.team7.rupiapp.dto.auth.signup.ResendVerificationDto;
import com.team7.rupiapp.dto.auth.signup.SetPasswordDto;
import com.team7.rupiapp.dto.auth.signup.SignupDto;
import com.team7.rupiapp.dto.auth.verify.VerificationDto;
import com.team7.rupiapp.enums.VerificationType;
import com.team7.rupiapp.service.AuthenticationService;
import com.team7.rupiapp.util.ApiResponseUtil;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController implements AuthApi {
    private final AuthenticationService authenticationService;

    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<Object> signup(@Valid @RequestBody SignupDto signupDto) {
        return ApiResponseUtil.success(HttpStatus.CREATED, "Signup success", authenticationService.signup(signupDto));
    }

    @PostMapping("/signin")
    public ResponseEntity<Object> signin(@Valid @RequestBody SigninDto signinDto) {
        SigninResponseDto response = authenticationService.signin(signinDto);
        return ApiResponseUtil.success(HttpStatus.OK, "Signin success", response);
    }

    @PostMapping("/verify/resend")
    public ResponseEntity<Object> resendVerificationEmail(
            @Valid @RequestBody ResendVerificationDto resendVerificationDto) {
        authenticationService.resendVerification(resendVerificationDto);
        return ApiResponseUtil.success(HttpStatus.OK, "Resend verification success");
    }

    @PostMapping("/verify")
    public ResponseEntity<Object> verify(@RequestHeader(value = "User-Agent") String userAgent, // TODO: remove me
            @Valid @RequestBody VerificationDto verificationDto,
            Principal principal) {
        log.info("User-Agent" + userAgent);
        if (principal == null && verificationDto.getType() != VerificationType.FORGOT_PASSWORD) {
            throw new BadCredentialsException("Unauthorized");
        }
        return authenticationService.verify(principal, verificationDto);
    }

    @PostMapping("/forgot-username")
    public ResponseEntity<Object> forgotUsernameRequest(@Valid @RequestBody ForgotUsernameDto forgotUsernameDto) {
        String response = authenticationService.forgotUsername(forgotUsernameDto);
        return ApiResponseUtil.success(HttpStatus.OK, response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Object> forgotPasswordRequest(@Valid @RequestBody ForgotPasswordDto forgotPasswordDto) {
        authenticationService.forgotPassword(forgotPasswordDto);
        return ApiResponseUtil.success(HttpStatus.OK, "Forgot password success");
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<Object> refreshToken(@Valid @RequestBody RefreshTokenDto refreshTokenDto) {
        return ApiResponseUtil.success(HttpStatus.OK, "Successfully refreshed token",
                authenticationService.refreshToken(refreshTokenDto));
    }

    @PostMapping("/set-password")
    public ResponseEntity<Object> setPassword(@Valid @RequestBody SetPasswordDto setPasswordDto, Principal principal) {
        authenticationService.setPassword(principal, setPasswordDto);
        return ApiResponseUtil.success(HttpStatus.OK, "Password has been successfully updated.");
    }

    @PostMapping("/set-pin")
    public ResponseEntity<Object> setPin(@Valid @RequestBody SetPinDto setPinDto, Principal principal) {
        authenticationService.setPin(principal, setPinDto);
        return ApiResponseUtil.success(HttpStatus.OK, "PIN has been successfully set.");
    }

    @PostMapping("/signout")
    public ResponseEntity<Object> signOut() {
        authenticationService.signOut();
        return ApiResponseUtil.success(HttpStatus.OK, "Signout success");
    }
}
