package com.team7.rupiapp.dto.account;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AccountMutationSummaryResponseDto {
    private Double totalIncome;
    private Double totalExpense;
    private Double totalEarnings;
    private LocalDateTime rangeStartMutationDate;
    private LocalDateTime rangeEndMutationDate;
}
