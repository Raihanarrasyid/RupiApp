package com.team7.rupiapp.service;

import com.team7.rupiapp.dto.account.AccountDetailResponseDto;
import com.team7.rupiapp.dto.account.AccountMutationSummaryResponseDto;
import com.team7.rupiapp.dto.account.AccountMutationsMonthlyDto;

import java.security.Principal;

public interface AccountService {
    public AccountDetailResponseDto getAccountDetail(Principal principal);
    public AccountMutationSummaryResponseDto getAccountMutationSummary(Principal principal);

<<<<<<< HEAD
    AccountMutationsMonthlyDto getAccountMutation(Principal principal);
    AccountMutationsMonthlyDto getAccountMutationPageable(Principal principal, int page, int size);
=======
    public AccountMutationsMonthlyDto getAccountMutation(Principal principal);
>>>>>>> fd27da403db9c7567aaf016cd0174cc4d3e95191
}
