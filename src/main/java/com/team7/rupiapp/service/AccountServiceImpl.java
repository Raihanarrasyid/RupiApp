package com.team7.rupiapp.service;

import com.team7.rupiapp.dto.account.AccountDetailResponseDto;
import com.team7.rupiapp.dto.account.AccountMutationResponseDto;
import com.team7.rupiapp.dto.account.AccountMutationsMonthlyDto;
import com.team7.rupiapp.exception.DataNotFoundException;
import com.team7.rupiapp.model.Mutation;
import com.team7.rupiapp.model.User;
import com.team7.rupiapp.repository.MutationRepository;
import com.team7.rupiapp.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl implements AccountService {
    private final UserRepository userRepository;
    private final MutationRepository mutationRepository;

    public AccountServiceImpl(UserRepository userRepository, MutationRepository mutationRepository) {
        this.userRepository = userRepository;
        this.mutationRepository = mutationRepository;
    }

    @Override
    public AccountDetailResponseDto getAccountDetail(Principal principal) {
        try {
            // Validate user
            User foundUser = userRepository.findByUsername(principal.getName())
                    .orElseThrow(() -> new DataNotFoundException("User not found"));

            AccountDetailResponseDto response = new AccountDetailResponseDto();
            response.setFullName(foundUser.getFullName());
            response.setEmail(foundUser.getEmail());
            response.setAccountNumber(foundUser.getAccountNumber());
            response.setBalance(foundUser.getBalance());

            return response;
        } catch (Exception e) {
            throw e;
        }
    }

    public AccountMutationsMonthlyDto getAccountMutation(Principal principal) {
        Optional<User> optionalUser = userRepository.findByUsername(principal.getName());

        UUID userId = optionalUser.map(User::getId).orElseThrow(() -> new RuntimeException("User not found"));

        List<Mutation> mutations = mutationRepository.findByUserId(userId);

        Map<Month, List<Mutation>> groupedByMonth = mutations.stream()
                .collect(Collectors.groupingBy(mutation -> mutation.getCreatedAt().getMonth()));

        Map<String, List<AccountMutationResponseDto>> responseData = new LinkedHashMap<>();

        for (Month month : Month.values()) {
            List<AccountMutationResponseDto> monthData = groupedByMonth.getOrDefault(month, Collections.emptyList()).stream()
                    .map(mutation -> {
                        AccountMutationResponseDto dto = new AccountMutationResponseDto();
                        dto.setDate(mutation.getCreatedAt());
                        dto.setCategory(mutation.getMutationType().name());
                        dto.setDescription(mutation.getDescription());
                        dto.setAmount(mutation.getAmount());
                        return dto;
                    })
                    .collect(Collectors.toList());

            responseData.put(month.name().toLowerCase(), monthData);
        }

        AccountMutationsMonthlyDto response = new AccountMutationsMonthlyDto();
        response.setData(responseData);

        return response;
    }
}
