package com.team7.rupiapp.dto.account;

import com.team7.rupiapp.enums.TransactionType;
import java.time.LocalDate;

import lombok.Data;

@Data
public class MutationDto {
    private String search;

    private LocalDate startDate;

    private LocalDate endDate;

    private TransactionType category;
}
