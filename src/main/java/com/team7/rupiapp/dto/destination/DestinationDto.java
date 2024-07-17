package com.team7.rupiapp.dto.destination;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DestinationDto {
    private String fullname;

    @Column(length = 10, unique = true)
    private String accountNumber;

    private boolean isFavorites;
}
