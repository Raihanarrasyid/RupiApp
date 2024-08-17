package com.team7.rupiapp.dto.transfer.qris;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class QrisGenerateCPMDto {
    @NotBlank(message = "PIN must not be blank")
    private String pin;
}
