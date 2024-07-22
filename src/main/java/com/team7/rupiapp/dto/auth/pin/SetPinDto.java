package com.team7.rupiapp.dto.auth.pin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SetPinDto {
    @NotBlank(message = "PIN is required")
    @Size(min = 6, max = 6, message = "pin must be 6 characters long")
    private String pin;

    @NotBlank(message = "Confirm PIN is required")
    private String confirmPin;
}
