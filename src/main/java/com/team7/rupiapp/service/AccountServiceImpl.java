package com.team7.rupiapp.service;

import com.team7.rupiapp.dto.account.AccountDetailResponseDto;
import com.team7.rupiapp.dto.account.AccountMutationResponseDto;
import com.team7.rupiapp.dto.account.AccountMutationSummaryResponseDto;
import com.team7.rupiapp.dto.account.AccountMutationsMonthlyDto;
import com.team7.rupiapp.enums.TransactionType;
import com.team7.rupiapp.exception.DataNotFoundException;
import com.team7.rupiapp.model.Mutation;
import com.team7.rupiapp.model.User;
import com.team7.rupiapp.repository.MutationRepository;
import com.team7.rupiapp.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
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
        User foundUser = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new DataNotFoundException("User not found"));

        AccountDetailResponseDto response = new AccountDetailResponseDto();
        response.setFullName(foundUser.getFullName());
        response.setEmail(foundUser.getEmail());
        response.setAccountNumber(foundUser.getAccountNumber());
        response.setBalance(foundUser.getBalance());

        return response;
    }

    @Override
    public AccountMutationSummaryResponseDto getAccountMutationSummary(Principal principal) {
        User foundUser = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new DataNotFoundException("User not found"));

        List<Mutation> mutations = mutationRepository.findByUserId(foundUser.getId());

        mutations = mutations.stream()
                .filter(mutation -> mutation.getCreatedAt().getMonth().equals(LocalDateTime.now().getMonth()))
                .toList();

        Double totalIncome = mutations.stream()
                .filter(mutation -> mutation.getTransactionType().equals(TransactionType.CREDIT))
                .mapToDouble(Mutation::getAmount)
                .sum();

        Double totalExpense = mutations.stream()
                .filter(mutation -> mutation.getTransactionType().equals(TransactionType.DEBIT))
                .mapToDouble(Mutation::getAmount)
                .sum();

        AccountMutationSummaryResponseDto response = new AccountMutationSummaryResponseDto();
        response.setTotalIncome(totalIncome);
        response.setTotalExpense(totalExpense);
        response.setTotalEarnings(totalIncome - totalExpense);
        response.setRangeStartMutationDate(LocalDateTime.now().withDayOfMonth(1));
        response.setRangeEndMutationDate(LocalDateTime.now().withDayOfMonth(LocalDateTime.now().toLocalDate().lengthOfMonth()));

        return response;
    }

    @Override
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

    public AccountMutationsMonthlyDto getAccountMutationPageable(Principal principal, int page, int size) {
        Optional<User> optionalUser = userRepository.findByUsername(principal.getName());

        UUID userId = optionalUser.map(User::getId).orElseThrow(() -> new RuntimeException("User not found"));

        Pageable pageable = PageRequest.of(page, size);
        Page<Mutation> mutationsPage = mutationRepository.findByUserId(userId, pageable);
        List<Mutation> mutations = mutationsPage.getContent();

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
                        dto.setAccountNumber(mutation.getAccountNumber());
                        dto.setTransactionPurpose(mutation.getTransactionPurpose().name());
                        dto.setTransactionType(mutation.getTransactionType().name());
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
