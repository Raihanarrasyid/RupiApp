package com.team7.rupiapp.service;

import com.team7.rupiapp.dto.account.AccountDetailResponseDto;
import com.team7.rupiapp.dto.account.AccountMutationSummaryResponseDto;
import com.team7.rupiapp.dto.account.AccountMutationsDto;

import java.security.Principal;

public interface AccountService {
    public AccountDetailResponseDto getAccountDetail(Principal principal);
    public AccountMutationSummaryResponseDto getAccountMutationSummary(Principal principal,
                                                                       Integer year,
                                                                       Integer month);

    public AccountMutationsDto getAccountMutationPageable(Principal principal, int page, int size, Integer year, Integer month, String transactionPurpose, String transactionType, String mutationType);
}
