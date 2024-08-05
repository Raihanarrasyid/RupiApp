package com.team7.rupiapp.dto.qris;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class QrisDto {
    @NotBlank(message = "Email is required")
    private String qris;

    private String amount;
}
