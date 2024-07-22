package com.team7.rupiapp.dto.destination;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
public class DestinationDto {
    private UUID id;
    private String fullname;

    @Column(length = 10)
    private String accountNumber;

    private boolean isFavorites;
}
