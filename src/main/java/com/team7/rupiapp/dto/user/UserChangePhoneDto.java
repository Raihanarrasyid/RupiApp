package com.team7.rupiapp.dto.user;

import com.team7.rupiapp.dto.validation.ValidPhoneNumber;
import com.team7.rupiapp.dto.validation.ValidUnique;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserChangePhoneDto {
    @NotBlank(message = "Phone is required")
    @ValidUnique(column = "phone", message = "Phone already been taken")
    @ValidPhoneNumber(message = "Phone must be a valid phone number")
    private String phone;
}
