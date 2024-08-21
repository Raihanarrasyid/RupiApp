package com.team7.rupiapp.dto.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

import com.team7.rupiapp.enums.MutationType;
import com.team7.rupiapp.enums.TransactionType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MutationResponseDto {
    private UUID id;
    private String fullName;
    private String accountNumber;
    private LocalDate date;
    private Double amount;
    private String description;
    private TransactionType transactionType;
    private MutationType mutationType;
}
