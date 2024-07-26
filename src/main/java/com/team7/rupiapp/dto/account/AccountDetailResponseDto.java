package com.team7.rupiapp.dto.account;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Account detail response")
public class AccountDetailResponseDto {

    @Schema(description = "Full name of account holder", example = "John Doe")
    private String fullName;

    @Schema(description = "Email of account holder", example = "john.doe@example.com")
    private String email;

    @Schema(description = "Account number", example = "1234567890")
    private String accountNumber;

    @Schema(description = "Account balance", example = "1500000.0")
    private Double balance;
}