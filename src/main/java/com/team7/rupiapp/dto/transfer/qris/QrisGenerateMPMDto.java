package com.team7.rupiapp.dto.transfer.qris;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class QrisGenerateMPMDto {
    @Min(value = 1, message = "Amount must be greater than zero")
    private Integer amount;
}
