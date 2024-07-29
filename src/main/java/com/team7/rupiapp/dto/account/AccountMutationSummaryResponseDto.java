package com.team7.rupiapp.dto.account;

import com.team7.rupiapp.enums.MutationType;
import com.team7.rupiapp.enums.TransactionPurpose;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@Schema(description = "Account mutation summary response")
public class AccountMutationSummaryResponseDto {

    private IncomeDetail income;
    private ExpenseDetail expense;

    @Schema(description = "Total earnings. Examples (quoted): '-Rp100.000,00', 'Rp0,00'", example = "-Rp100.000,00")
    private String totalEarnings;

    @Schema(description = "Range start mutation date. Examples (quoted): '2024-07-01T00:00:00', '2024-06-01T00:00:00'", example = "2024-07-01T00:00:00")
    private LocalDateTime rangeStartMutationDate;

    @Schema(description = "Range end mutation date. Examples (quoted): '2024-07-27T14:56:47.6634487', '2024-06-30T23:59:59.999999999'", example = "2024-07-27T14:56:47.6634487")
    private LocalDateTime rangeEndMutationDate;

    @Data
    @Builder
    @Schema(description = "Income detail")
    public static class IncomeDetail {
        private List<CategoryDetail> categories;

        @Schema(description = "Total income. Examples (quoted): 'Rp50.000,00', 'Rp0,00'", example = "Rp50.000,00")
        private String totalIncome;
        @Schema(description = "Total income percentage. Examples (quoted): '25', '0.0'", example = "25")
        private Double totalIncomePercentage;
    }

    @Data
    @Builder
    @Schema(description = "Expense detail")
    public static class ExpenseDetail {
        private List<CategoryDetail> categories;

        @Schema(description = "Total expense. Examples (quoted): 'Rp150.000,00', 'Rp0,00'", example = "Rp150.000,00")
        private String totalExpense;
        @Schema(description = "Total expense percentage. Examples (quoted): '75', '0.0'", example = "75")
        private Double totalExpensePercentage;
    }

    @Data
    @Builder
    @Schema(description = "Category detail")
    public static class CategoryDetail {
        private List<MutationDetail> mutations;

        @Schema(description = "Mutation type", implementation = MutationType.class, example = "QRIS")
        private MutationType type;
        @Schema(description = "Number of transactions. Examples (quoted): '5', '0'", example = "5")
        private Integer numberOfTransactions;
        @Schema(description = "Total balance. Examples (quoted): 'Rp50.000,00', 'Rp0,00'", example = "Rp50.000,00")
        private String totalBalance;
        @Schema(description = "Total balance percentage. Examples (quoted): '25', '0.0', '33.33', '12.5'",
                example = "33.33")
        private Double totalBalancePercentage;
    }

    @Data
    @Builder
    @Schema(description = "Mutation detail")
    public static class MutationDetail {
        private String accountNumber;
        private String fullName;
        private String amount;
        private String description;
        private TransactionPurpose transactionPurpose;
        private LocalDateTime createdAt;
    }

}
