package com.team7.rupiapp.service;

import com.team7.rupiapp.dto.account.AccountDetailResponseDto;

public interface AccountService {
    public AccountDetailResponseDto getAccountDetail(String username);
}
