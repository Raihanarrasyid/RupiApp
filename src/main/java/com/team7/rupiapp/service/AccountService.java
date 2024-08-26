package com.team7.rupiapp.service;

import com.team7.rupiapp.dto.account.AccountDetailResponseDto;
import com.team7.rupiapp.dto.account.AccountMutationSummaryResponseDto;
import com.team7.rupiapp.dto.account.MutationDto;
import com.team7.rupiapp.dto.account.MutationResponseDto;

import java.security.Principal;
import java.util.UUID;

import org.springframework.data.domain.Page;

public interface AccountService {
    public AccountDetailResponseDto getAccountDetail(Principal principal);

    public AccountMutationSummaryResponseDto getAccountMutationSummary(Principal principal, Integer year,
            Integer month);

    public Page<MutationResponseDto> getMutations(Principal principal, MutationDto mutationDto, int page, int size);

    public Object getMutationDetails(UUID mutationId, Principal principal);
}
