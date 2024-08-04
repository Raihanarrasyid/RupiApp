package com.team7.rupiapp.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.team7.rupiapp.dto.user.UserChangeEmailDto;
import com.team7.rupiapp.dto.user.UserChangePasswordDto;
import com.team7.rupiapp.dto.user.UserChangePhoneDto;
import com.team7.rupiapp.dto.user.UserChangePinDto;
import com.team7.rupiapp.dto.user.UserChangeProfileDto;
import com.team7.rupiapp.dto.user.UserVerifyOtpDto;
import com.team7.rupiapp.dto.user.UserVerifyPasswordDto;
import com.team7.rupiapp.dto.user.UserVerifyPinDto;
import com.team7.rupiapp.service.UserService;
import com.team7.rupiapp.util.ApiResponseUtil;

import jakarta.validation.Valid;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@RestController
@RequestMapping("/users")
public class UserController {
    UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ResponseEntity<Object> getUserProfile(Principal principal) {
        return ApiResponseUtil.success(HttpStatus.OK, "User profile", userService.getUserProfile(principal));
    }

    @PostMapping("/change-profile")
    public ResponseEntity<Object> changeProfile(Principal principal,
            @Valid @ModelAttribute UserChangeProfileDto userChangeProfileDto) {
        userService.changeProfile(principal, userChangeProfileDto);
        return ApiResponseUtil.success(HttpStatus.OK, "Profile has been changed");
    }

    @PostMapping("/change-email")
    public ResponseEntity<Object> changeEmail(Principal principal, @RequestBody UserChangeEmailDto userChangeEmailDto) {
        userService.changeEmail(principal, userChangeEmailDto);
        return ApiResponseUtil.success(HttpStatus.OK, "Email change request has been sent");
    }

    @PostMapping("/verify-email")
    public ResponseEntity<Object> verifyEmail(Principal principal,
            @Valid @RequestBody UserVerifyOtpDto userVerifyOtpDto) {
        userService.verifyEmail(principal, userVerifyOtpDto);
        return ApiResponseUtil.success(HttpStatus.OK, "Email has been verified");
    }

    @PostMapping("/change-number")
    public ResponseEntity<Object> changeNumber(Principal principal,
            @Valid @RequestBody UserChangePhoneDto userChangePhoneDto) {
        userService.changeNumber(principal, userChangePhoneDto);
        return ApiResponseUtil.success(HttpStatus.OK, "Number change request has been sent");
    }

    @PostMapping("/verify-number")
    public ResponseEntity<Object> verifyNumber(Principal principal,
            @Valid @RequestBody UserVerifyOtpDto userVerifyOtpDto) {
        userService.verifyNumber(principal, userVerifyOtpDto);
        return ApiResponseUtil.success(HttpStatus.OK, "Number has been verified");
    }

    @PostMapping("/verify-password")
    public ResponseEntity<Object> verifyPassword(Principal principal,
            @Valid @RequestBody UserVerifyPasswordDto userVerifyPasswordDto) {
        return ApiResponseUtil.success(HttpStatus.OK, "Password has been verified",
                userService.verifyPassword(principal, userVerifyPasswordDto));
    }

    @PostMapping("/change-password")
    public ResponseEntity<Object> changePassword(Principal principal, @RequestHeader("X-SIGNATURE") String signature,
            @Valid @RequestBody UserChangePasswordDto userChangePasswordDto) {
        userService.changePassword(principal, signature, userChangePasswordDto);
        return ApiResponseUtil.success(HttpStatus.OK, "Password has been changed");
    }

    @PostMapping("/verify-pin")
    public ResponseEntity<Object> verifyPin(Principal principal,
            @Valid @RequestBody UserVerifyPinDto userVerifyPinDto) {
        return ApiResponseUtil.success(HttpStatus.OK, "Pin has been verified",
                userService.verifyPin(principal, userVerifyPinDto));
    }

    @PostMapping("/change-pin")
    public ResponseEntity<Object> changePin(Principal principal, @RequestHeader("X-SIGNATURE") String signature,
            @Valid @RequestBody UserChangePinDto userChangePinDto) {
        userService.changePin(principal, signature, userChangePinDto);
        return ApiResponseUtil.success(HttpStatus.OK, "Pin has been changed");
    }
}
