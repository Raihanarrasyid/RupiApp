package com.team7.rupiapp.dto.account;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class AccountMutationsMonthlyDto {
    private Map<String, List<AccountMutationResponseDto>> data;
}