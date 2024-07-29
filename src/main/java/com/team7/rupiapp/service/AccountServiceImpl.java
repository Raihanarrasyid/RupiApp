package com.team7.rupiapp.service;

import com.team7.rupiapp.dto.account.AccountDetailResponseDto;
import com.team7.rupiapp.dto.account.AccountMutationResponseDto;
import com.team7.rupiapp.dto.account.AccountMutationSummaryResponseDto;
import com.team7.rupiapp.dto.account.AccountMutationsMonthlyDto;
import com.team7.rupiapp.enums.TransactionType;
import com.team7.rupiapp.exception.BadRequestException;
import com.team7.rupiapp.exception.DataNotFoundException;
import com.team7.rupiapp.model.Mutation;
import com.team7.rupiapp.model.User;
import com.team7.rupiapp.repository.MutationRepository;
import com.team7.rupiapp.repository.UserRepository;
import com.team7.rupiapp.util.CurrencyFormatter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.TemporalAdjusters;
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
        response.setBalance(CurrencyFormatter.formatToIDR(foundUser.getBalance()));

        return response;
    }

    @Override
    public AccountMutationSummaryResponseDto getAccountMutationSummary(Principal principal,
                                                                       Integer month,
                                                                       Integer year) {

        User foundUser = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new DataNotFoundException("User not found"));

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime rangeStartMutationDate = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime rangeEndMutationDate = getRangeEndMutationLocalDateTime(now, rangeStartMutationDate, year, month);

        List<Mutation> mutations = mutationRepository.findByUserIdAndCreatedAtBetween(
                foundUser.getId(), rangeStartMutationDate, rangeEndMutationDate);

        double totalIncome = mutations.stream()
                .filter(mutation -> mutation.getTransactionType().equals(TransactionType.CREDIT))
                .mapToDouble(Mutation::getAmount)
                .sum();
        double totalExpense = mutations.stream()
                .filter(mutation -> mutation.getTransactionType().equals(TransactionType.DEBIT))
                .mapToDouble(Mutation::getAmount)
                .sum();
        double denominatorBalance = totalIncome + totalExpense;
        double totalEarnings = totalIncome - totalExpense;

        AccountMutationSummaryResponseDto response = new AccountMutationSummaryResponseDto();
        response.setTotalIncome(CurrencyFormatter.formatToIDR(totalIncome));
        response.setTotalExpense(CurrencyFormatter.formatToIDR(totalExpense));
        response.setTotalIncomePercentage(calculateBalancePercentage(totalIncome, denominatorBalance));
        response.setTotalExpensePercentage(calculateBalancePercentage(totalExpense, denominatorBalance));
        response.setTotalEarnings(CurrencyFormatter.formatToIDR(totalEarnings));
        response.setRangeStartMutationDate(rangeStartMutationDate);
        response.setRangeEndMutationDate(rangeEndMutationDate);

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

    public AccountMutationsMonthlyDto getAccountMutationPageable(Principal principal, int page, int size,
                                                                 Integer year, Integer month, String transactionPurpose, String transactionType, String mutationType) {
        Optional<User> optionalUser = userRepository.findByUsername(principal.getName());

        UUID userId = optionalUser.map(User::getId).orElseThrow(() -> new RuntimeException("User not found"));

        Pageable pageable = PageRequest.of(page, size);

        Page<Mutation> mutationsPage;
        if (year != null && month != null) {
            LocalDateTime startDate = LocalDateTime.of(year, month, 1, 0, 0);
            LocalDateTime endDate = startDate.withDayOfMonth(startDate.toLocalDate().lengthOfMonth());
            mutationsPage = mutationRepository.findByUserIdAndCreatedAtBetween(userId, startDate, endDate, pageable);
        } else {
            mutationsPage = mutationRepository.findByUserId(userId, pageable);
        }

        List<Mutation> mutations = mutationsPage.getContent();

        if (transactionPurpose != null) {
            mutations = mutations.stream()
                    .filter(mutation -> mutation.getTransactionPurpose().name().equalsIgnoreCase(transactionPurpose))
                    .collect(Collectors.toList());
        }

        if (transactionType != null) {
            mutations = mutations.stream()
                    .filter(mutation -> mutation.getTransactionType().name().equalsIgnoreCase(transactionType))
                    .collect(Collectors.toList());
        }

        if (mutationType != null) {
            mutations = mutations.stream()
                    .filter(mutation -> mutation.getMutationType().name().equalsIgnoreCase(mutationType))
                    .collect(Collectors.toList());
        }

        Map<Month, List<Mutation>> groupedByMonth = mutations.stream()
                .collect(Collectors.groupingBy(mutation -> mutation.getCreatedAt().getMonth()));

        Map<String, List<AccountMutationResponseDto>> responseData = new LinkedHashMap<>();

        for (Month monthEnum : Month.values()) {
            List<AccountMutationResponseDto> monthData = groupedByMonth.getOrDefault(monthEnum, Collections.emptyList()).stream()
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

            responseData.put(monthEnum.name().toLowerCase(), monthData);
        }

        AccountMutationsMonthlyDto response = new AccountMutationsMonthlyDto();
        response.setData(responseData);

        return response;
    }

    private static LocalDateTime getRangeEndMutationLocalDateTime(LocalDateTime now, LocalDateTime rangeStartMutationDate, Integer year, Integer month) {
        LocalDateTime rangeEndMutationDate;

        if (year == now.getYear() && month == now.getMonth().getValue()) {
            rangeEndMutationDate = now;
        } else if (rangeStartMutationDate.isAfter(now)) {
            throw new BadRequestException("Cannot query data for future dates.");
        } else {
            rangeEndMutationDate = rangeStartMutationDate
                    .with(TemporalAdjusters.lastDayOfMonth())
                    .withHour(23).withMinute(59).withSecond(59).withNano(999999999);
        }
        return rangeEndMutationDate;
    }

    private double calculateBalancePercentage(double numerator, double denominator) {
        return (denominator != 0) ? (numerator / denominator * 100) : 0.0;
    }

}
