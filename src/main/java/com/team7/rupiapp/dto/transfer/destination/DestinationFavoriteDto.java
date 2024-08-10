package com.team7.rupiapp.dto.transfer.destination;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DestinationFavoriteDto {
    @NotNull(message = "favorites must not be null")
    private Boolean isFavorites;
}
