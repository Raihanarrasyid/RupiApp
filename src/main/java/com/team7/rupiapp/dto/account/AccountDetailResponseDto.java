package com.team7.rupiapp.dto.account;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
@Schema(description = "Account detail response")
public class AccountDetailResponseDto {

    @Schema(description = "User ID", example = "0bacf52e-f940-4cfa-9c5b-ed1508f5e2d1")
    private UUID userId;

    @Schema(description = "Full name of account holder", example = "John Doe")
    private String fullName;

    @Schema(description = "Email of account holder", example = "john.doe@example.com")
    private String email;

    @Schema(description = "Account number", example = "0774014731")
    private String accountNumber;

    @Schema(description = "Account balance. Examples (quoted): 'Rp3.900.000,00', 'Rp0,00'", example = "Rp3.900.000,00")
    private String balance;
}