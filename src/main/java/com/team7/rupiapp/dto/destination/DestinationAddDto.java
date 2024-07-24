package com.team7.rupiapp.dto.destination;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NonNull;

@Data
public class DestinationAddDto {
    private String fullname;
    @Column(length = 10)
    @NotNull(message = "account_number is required")
    private String accountNumber;
}
