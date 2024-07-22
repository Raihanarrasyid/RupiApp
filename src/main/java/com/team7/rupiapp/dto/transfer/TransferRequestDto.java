package com.team7.rupiapp.dto.transfer;

import com.team7.rupiapp.enums.MutationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class TransferRequestDto {
    @NotNull(message = "Destination ID must not be null")
    private UUID destinationId;

    @NotNull(message = "Amount must not be null")
    private Double amount;

    private String description;

    @NotNull(message = "Type must not be null")
    private MutationType type;

    @NotBlank(message = "PIN must not be blank")
    private String pin;
}
