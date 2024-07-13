package com.team7.rupiapp.dto.auth.pin;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PinDto {
    @NotBlank(message = "pin must not be null")
    private String pin;
}
