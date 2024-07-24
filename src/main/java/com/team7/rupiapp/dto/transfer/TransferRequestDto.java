package com.team7.rupiapp.dto.transfer;

import com.team7.rupiapp.enums.MutationType;
import com.team7.rupiapp.enums.TransactionPurpose;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class TransferRequestDto {
    @NotNull(message = "Destination ID must not be null")
    private UUID destinationId;

    @NotNull(message = "Amount must not be null")
    @Min(value = 1, message = "Amount must be greater than zero")
    private Double amount;

    private String description;

    @NotNull(message = "Type must not be null")
    private MutationType type;

    @NotBlank(message = "PIN must not be blank")
    private String pin;

    @NotNull(message = "Transaction Purpose must not be null")
    private TransactionPurpose transactionPurpose;
}