package com.team7.rupiapp.dto.account;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "Account mutation summary response")
public class AccountMutationSummaryResponseDto {

    @Schema(description = "Total income. Examples (quoted): 'Rp50.000,00', 'Rp0,00'", example = "Rp50.000,00")
    private String totalIncome;

    @Schema(description = "Total expense. Examples (quoted): 'Rp150.000,00', 'Rp0,00'", example = "Rp150.000,00")
    private String totalExpense;

    @Schema(description = "Total income percentage. Examples (quoted): '25', '0.0'", example = "25")
    private Double totalIncomePercentage;

    @Schema(description = "Total expense percentage. Examples (quoted): '75', '0.0'", example = "75")
    private Double totalExpensePercentage;

    @Schema(description = "Total earnings. Examples (quoted): '-Rp100.000,00', 'Rp0,00'", example = "-Rp100.000,00")
    private String totalEarnings;

    @Schema(description = "Range start mutation date. Examples (quoted): '2024-07-01T00:00:00', '2024-06-01T00:00:00'", example = "2024-07-01T00:00:00")
    private LocalDateTime rangeStartMutationDate;

    @Schema(description = "Range end mutation date. Examples (quoted): '2024-07-27T14:56:47.6634487', '2024-06-30T23:59:59.999999999'", example = "2024-07-27T14:56:47.6634487")
    private LocalDateTime rangeEndMutationDate;
}
