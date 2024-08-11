package com.team7.rupiapp.dto.transfer.destination;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DestinationAddDto {
    private String fullname;
    @Column(length = 10)
    @NotNull(message = "account_number is required")
    private String accountNumber;
    private String destinationId;
}
