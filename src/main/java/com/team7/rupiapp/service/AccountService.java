package com.team7.rupiapp.service;

import com.team7.rupiapp.dto.account.AccountDetailResponseDto;
import com.team7.rupiapp.dto.account.AccountMutationSummaryResponseDto;
import com.team7.rupiapp.dto.account.AccountMutationsMonthlyDto;

import java.security.Principal;

public interface AccountService {
    public AccountDetailResponseDto getAccountDetail(Principal principal);
    public AccountMutationSummaryResponseDto getAccountMutationSummary(Principal principal);


    public AccountMutationsMonthlyDto getAccountMutation(Principal principal);
    public AccountMutationsMonthlyDto getAccountMutationPageable(Principal principal, int page, int size);
}
