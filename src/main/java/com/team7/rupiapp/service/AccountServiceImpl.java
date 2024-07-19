package com.team7.rupiapp.service;

import com.team7.rupiapp.dto.account.AccountDetailResponseDto;
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
        Optional<User> foundUser = userRepository.findByUsername(principal.getName());

        AccountDetailResponseDto response = new AccountDetailResponseDto();
        foundUser.ifPresentOrElse(user -> {
            response.setFullName(user.getFullName());
            response.setEmail(user.getEmail());
            response.setAccountNumber(user.getAccountNumber());
            response.setBalance(user.getBalance());
        }, () -> {
            throw new DataNotFoundException("User not found");
        });

        return response;
    }

    public Map<String, List<Map<String, Object>>> getAccountMutation(Principal principal) {
        Optional<User> optionalUser = userRepository.findByUsername(principal.getName());

        UUID userId = optionalUser.map(User::getId).orElseThrow(() -> new RuntimeException("User not found"));
        List<Mutation> mutations = mutationRepository.findByUserId(userId);
        Map<Month, List<Mutation>> groupedByMonth = mutations.stream()
                .collect(Collectors.groupingBy(mutation -> mutation.getCreatedAt().getMonth()));

        Map<String, List<Map<String, Object>>> response = new LinkedHashMap<>();

        for (Month month : Month.values()) {
            List<Map<String, Object>> monthData = groupedByMonth.getOrDefault(month, Collections.emptyList()).stream()
                    .map(mutation -> {
                        Map<String, Object> data = new HashMap<>();
                        data.put("date", mutation.getCreatedAt().toString());
                        data.put("category", mutation.getType().name());
                        data.put("description", mutation.getDescription());
                        data.put("amount", mutation.getAmount());
                        return data;
                    })
                    .collect(Collectors.toList());

            response.put(month.name().toLowerCase(), monthData);
        }

        return response;
    }
}
