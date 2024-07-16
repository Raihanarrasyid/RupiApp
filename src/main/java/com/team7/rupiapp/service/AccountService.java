package com.team7.rupiapp.service;

import com.team7.rupiapp.dto.account.AccountDetailResponseDto;

import java.security.Principal;

public interface AccountService {
    public AccountDetailResponseDto getAccountDetail(Principal principal);
}
