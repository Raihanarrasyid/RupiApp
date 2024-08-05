package com.team7.rupiapp.dto.qris;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class QrisDto {
    @NotBlank(message = "QRIS code must not be blank")
    private String qris;

    @Min(value = 1, message = "Amount must be greater than zero")
    private String amount;

    private String description;

    @NotBlank(message = "PIN must not be blank")
    private String pin;
}
