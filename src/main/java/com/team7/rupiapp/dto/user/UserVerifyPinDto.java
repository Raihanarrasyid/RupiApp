package com.team7.rupiapp.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserVerifyPinDto {
    @NotBlank(message = "PIN is required")
    private String pin;
}
