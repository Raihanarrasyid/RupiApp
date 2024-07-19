package com.team7.rupiapp.service;

import com.team7.rupiapp.dto.account.AccountMutationResponseDto;
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
public class MutationServiceImpl {
    private final MutationRepository mutationRepository;
    private final UserRepository userRepository;

    public MutationServiceImpl(MutationRepository mutationRepository, UserRepository userRepository) {
        this.mutationRepository = mutationRepository;
        this.userRepository = userRepository;
    }

    public Map<String, List<Map<String, String>>> getAccountMutation(Principal principal) {
        Optional<User> optionalUser = userRepository.findByUsername(principal.getName());

        UUID userId = optionalUser.map(User::getId).orElseThrow(() -> new RuntimeException("User not found"));
        List<Mutation> mutations = mutationRepository.findByUserId(userId);
        Map<Month, List<Mutation>> groupedByMonth = mutations.stream()
                .collect(Collectors.groupingBy(mutation -> mutation.getCreatedAt().getMonth()));

        Map<String, List<Map<String, String>>> response = new LinkedHashMap<>();

        for (Month month : Month.values()) {
            List<Map<String, String>> monthData = groupedByMonth.getOrDefault(month, Collections.emptyList()).stream()
                    .map(mutation -> {
                        Map<String, String> data = new HashMap<>();
                        data.put("date", mutation.getCreatedAt().toString());
                        data.put("category", mutation.getType().name());
                        data.put("description", mutation.getDescription());
                        return data;
                    })
                    .collect(Collectors.toList());

            response.put(month.name().toLowerCase(), monthData);
        }

        return response;
    }

}
