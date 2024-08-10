package com.team7.rupiapp.dto.transfer.destination;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class DestinationDto {
    private UUID id;
    private String fullname;

    @Size(min = 10, max = 10, message = "account_number must be 10 characters")
    private String accountNumber;

    private boolean isFavorites;
}
