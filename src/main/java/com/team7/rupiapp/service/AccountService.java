package com.team7.rupiapp.service;

import com.team7.rupiapp.dto.account.AccountDetailResponseDto;
import com.team7.rupiapp.dto.account.AccountMutationsMonthlyDto;

import java.security.Principal;
import java.util.List;
import java.util.Map;

public interface AccountService {
    public AccountDetailResponseDto getAccountDetail(Principal principal);

    AccountMutationsMonthlyDto getAccountMutation(Principal principal);
}
