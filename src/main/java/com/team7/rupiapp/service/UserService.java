package com.team7.rupiapp.service;

import java.security.Principal;

import com.team7.rupiapp.dto.user.UserChangeEmailDto;
import com.team7.rupiapp.dto.user.UserChangePasswordDto;
import com.team7.rupiapp.dto.user.UserChangePhoneDto;
import com.team7.rupiapp.dto.user.UserChangePinDto;
import com.team7.rupiapp.dto.user.UserChangeProfileDto;
import com.team7.rupiapp.dto.user.UserProfileResponseDto;
import com.team7.rupiapp.dto.user.UserSignatureResponseDto;
import com.team7.rupiapp.dto.user.UserVerifyOtpDto;
import com.team7.rupiapp.dto.user.UserVerifyPasswordDto;
import com.team7.rupiapp.dto.user.UserVerifyPinDto;

public interface UserService {
    public UserProfileResponseDto getUserProfile(Principal principal);

    public void changeProfile(Principal principal, UserChangeProfileDto userChangeProfileDto);

    public void changeEmail(Principal principal, UserChangeEmailDto userChangeEmailDto);

    public void resendEmail(Principal principal);

    public void verifyEmail(Principal principal, UserVerifyOtpDto userVerifyOtpDto);

    public void resendNumber(Principal principal);

    public void changeNumber(Principal principal, UserChangePhoneDto userChangePhoneDto);

    public void verifyNumber(Principal principal, UserVerifyOtpDto userVerifyOtpDto);

    public UserSignatureResponseDto verifyPassword(Principal principal, UserVerifyPasswordDto userVerifyPasswordDto);

    public void changePassword(Principal principal, String signature, UserChangePasswordDto userChangePasswordDto);

    public UserSignatureResponseDto verifyPin(Principal principal, UserVerifyPinDto userVerifyPinDto);

    public void changePin(Principal principal, String signature, UserChangePinDto userChangePinDto);
}
