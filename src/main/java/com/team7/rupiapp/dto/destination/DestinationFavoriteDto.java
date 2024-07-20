package com.team7.rupiapp.dto.destination;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DestinationFavoriteDto {
    @NotNull(message = "Favorite status must not be null")
    private boolean isFavorites;
}
