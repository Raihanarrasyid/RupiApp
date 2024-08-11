package com.team7.rupiapp.dto.auth.forgot;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ForgotUsernameDto {
    @NotBlank(message = "Destination is required")
    private String destination;
}
