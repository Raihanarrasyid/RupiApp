package com.team7.rupiapp.dto.demo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DemoQrisCPMDto {
    @NotBlank(message = "Qris is required")
    private String qris;

    @NotBlank(message = "Merchant is required")
    private String merchant;

    @NotNull(message = "Amount is required")
    private Double amount;
}
