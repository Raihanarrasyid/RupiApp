package com.team7.rupiapp.dto.account;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AccountMutationResponseDto {
    private LocalDateTime date;
    private String category;
    private String description;
    private Double amount;
}
