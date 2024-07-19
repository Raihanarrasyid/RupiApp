package com.team7.rupiapp.service;

import com.team7.rupiapp.dto.account.AccountDetailResponseDto;

import java.security.Principal;
import java.util.List;
import java.util.Map;

public interface AccountService {
    public AccountDetailResponseDto getAccountDetail(Principal principal);

    Map<String, List<Map<String, Object>>> getAccountMutation(Principal principal);
}
