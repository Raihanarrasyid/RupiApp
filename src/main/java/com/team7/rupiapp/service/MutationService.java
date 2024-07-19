package com.team7.rupiapp.service;

import com.team7.rupiapp.dto.account.AccountMutationResponseDto;

import java.security.Principal;
import java.util.List;
import java.util.Map;

public interface MutationService {
    Map<String, List<Map<String, String>>> getMutation(Principal principal);
}
