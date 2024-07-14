package com.team7.rupiapp.dto.auth.pin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SetPinDto {
    @NotBlank(message = "pin must not be null")
    @Size(min = 6, max = 6, message = "pin must be 6 characters long")
    private String pin;

    @NotBlank(message = "confirm_pin must not be null")
    private String confirmPin;
}
