package com.team7.rupiapp.dto.transfer.destination;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DestinationAddDto {
    private String fullname;
    @Column(length = 10)
    @NotBlank(message = "Account number must not be empty")
    @NotNull(message = "Account number must not be null")
    private String accountNumber;
    private String destinationId;
}
