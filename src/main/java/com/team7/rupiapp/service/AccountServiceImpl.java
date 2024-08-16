package com.team7.rupiapp.service;

import com.team7.rupiapp.dto.account.AccountDetailResponseDto;
import com.team7.rupiapp.dto.account.AccountMutationResponseDto;
import com.team7.rupiapp.dto.account.AccountMutationSummaryResponseDto;
import com.team7.rupiapp.dto.account.AccountMutationsMonthlyDto;
import com.team7.rupiapp.enums.MutationType;
import com.team7.rupiapp.enums.TransactionType;
import com.team7.rupiapp.exception.BadRequestException;
import com.team7.rupiapp.exception.DataNotFoundException;
import com.team7.rupiapp.model.Mutation;
import com.team7.rupiapp.model.User;
import com.team7.rupiapp.repository.MutationRepository;
import com.team7.rupiapp.repository.UserRepository;
import com.team7.rupiapp.util.Formatter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.time.LocalDate;
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

        return AccountDetailResponseDto.builder()
                .userId(foundUser.getId())
                .fullName(foundUser.getFullName())
                .email(foundUser.getEmail())
                .accountNumber(foundUser.getAccountNumber())
                .balance(Formatter.formatToString(foundUser.getBalance()))
                .build();
    }

    @Override
    public AccountMutationSummaryResponseDto getAccountMutationSummary(Principal principal,
                                                                       Integer year,
                                                                       Integer month) {
        /*
        * rangeStartMutationDate:
        *       By default, the rangeStartMutationDate defined as the beginning of the month
        *               of the given year and month.
        *       If the 'year' and 'month' fields are provided, the rangeStartMutationDate will be
        *               set based on the respective fields.
        *       The date of month cannot be set to a future date. Reason: the data is not available yet.
        *
        * rangeEndMutationDate:
        *       By default, the rangeEndMutationDate defined as the end of the month.
        *       The date of month defined as the last day of the month or current date if
        *               the provided 'month' parameter is equals to current month.
        * */
        LocalDateTime rangeStartMutationDate = getRangeStartMutationLocalDateTime(year, month);
        LocalDateTime rangeEndMutationDate = getRangeEndMutationLocalDateTime(rangeStartMutationDate);

        User foundUser = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new DataNotFoundException("User not found"));

        List<Mutation> mutations = mutationRepository.findByUserIdAndCreatedAtBetween(
                foundUser.getId(), rangeStartMutationDate, rangeEndMutationDate);

        /*
         * The balance information (expense and income) calculations are explained as follows:
         *      totalIncome = 100
         *      totalExpense = 50
         *      denominatorBalance = totalIncome + totalExpense = 100 + 50 = 150 // In percentage: 100%
         *      totalIncomePercentage = (totalIncome / denominatorBalance) * 100 = (100 / 150) * 100 = 66.67%
         *      totalExpensePercentage = (totalExpense / denominatorBalance) * 100 = (50 / 150) * 100 = 33.33%
         *      totalEarnings = totalIncome - totalExpense = 100 - 50 = 50
         *
         * The category details are explained as follows:
         *      numberOfTransactions = 2 // fetched from the number of transactions in the category
         *      totalBalance = 100 // fetched from the total balance in the category
         *      totalBalancePercentage = (totalBalance / totalExpenseOrIncomeAsDenominatorBalance) * 100 // total
         *      balance percentage by TransactionType
         * */
        double totalIncome = calculateTotal(mutations, TransactionType.CREDIT);
        double totalExpense = calculateTotal(mutations, TransactionType.DEBIT);
        double denominatorBalance = totalIncome + totalExpense;
        double totalEarnings = totalIncome - totalExpense;

        List<AccountMutationSummaryResponseDto.CategoryDetail> creditCategories = getCategoryDetails(mutations,
                TransactionType.CREDIT, totalIncome, foundUser);
        List<AccountMutationSummaryResponseDto.CategoryDetail> debitCategories = getCategoryDetails(mutations,
                TransactionType.DEBIT, totalExpense, foundUser);

        return AccountMutationSummaryResponseDto.builder()
                .income(AccountMutationSummaryResponseDto.IncomeDetail.builder()
                        .categories(creditCategories)
                        .totalIncome(Formatter.formatToString(totalIncome))
                        .totalIncomePercentage(calculateBalancePercentage(totalIncome, denominatorBalance))
                        .build()
                )
                .expense(AccountMutationSummaryResponseDto.ExpenseDetail.builder()
                        .categories(debitCategories)
                        .totalExpense(Formatter.formatToString(totalExpense))
                        .totalExpensePercentage(calculateBalancePercentage(totalExpense, denominatorBalance))
                        .build()
                )
                .totalEarnings(Formatter.formatToString(totalEarnings))
                .rangeStartMutationDate(rangeStartMutationDate)
                .rangeEndMutationDate(rangeEndMutationDate)
                .build();
    }

    public AccountMutationsMonthlyDto getAccountMutationPageable(Principal principal, int page, int size,
                                                                 Integer year, Integer month, String transactionPurpose, String transactionType, String mutationType) {
        int currentYear = LocalDate.now().getYear();

        if (year != null && year > currentYear) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot query data for future dates.");
        }

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

    private static LocalDateTime getRangeStartMutationLocalDateTime(Integer year, Integer month) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime rangeStartMutationDate;

        if (year == null && month == null) {
            rangeStartMutationDate = LocalDateTime.of(now.getYear(), now.getMonth(), 1, 0, 0);
        } else if (year == null || month == null) {
            throw new BadRequestException("Both 'year' and 'month' parameters must be provided, or none at all.");
        } else {
            rangeStartMutationDate = LocalDateTime.of(year, month, 1, 0, 0);
        }

        return rangeStartMutationDate;
    }

    private static LocalDateTime getRangeEndMutationLocalDateTime(LocalDateTime rangeStartMutationDate) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime rangeEndMutationDate;

        if (rangeStartMutationDate.getYear() == now.getYear() && rangeStartMutationDate.getMonth() == now.getMonth()) {
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

    private double calculateTotal(List<Mutation> mutations, TransactionType transactionType) {
        return mutations.stream()
                .filter(mutation -> mutation.getTransactionType().equals(transactionType))
                .mapToDouble(Mutation::getAmount)
                .sum();
    }

    private List<AccountMutationSummaryResponseDto.CategoryDetail> getCategoryDetails(List<Mutation> mutations, TransactionType transactionType, double denominatorBalance, User user) {
        return mutations.stream()
                .filter(mutation -> mutation.getTransactionType().equals(transactionType))
                .collect(Collectors.groupingBy(
                        Mutation::getMutationType,
                        Collectors.summingDouble(Mutation::getAmount)))
                .entrySet().stream()
                .map(mutationsGroupedByMutationTypeSummedByMutationAmount -> AccountMutationSummaryResponseDto.CategoryDetail.builder()
                        .type(mutationsGroupedByMutationTypeSummedByMutationAmount.getKey())
                        .numberOfTransactions(
                                extractNumberOfTransaction(mutations, transactionType,
                                mutationsGroupedByMutationTypeSummedByMutationAmount.getKey()))
                        .totalBalance(Formatter.formatToString(mutationsGroupedByMutationTypeSummedByMutationAmount.getValue()))
                        .totalBalancePercentage(calculateBalancePercentage(mutationsGroupedByMutationTypeSummedByMutationAmount.getValue(), denominatorBalance))
                        .mutations(
                                extractMutations(mutations, transactionType,
                                mutationsGroupedByMutationTypeSummedByMutationAmount.getKey(), user))
                        .build())
                .toList();
    }

    private Integer extractNumberOfTransaction(List<Mutation> mutations, TransactionType transactionType,
                                               MutationType mutationType) {
        return Math.toIntExact(mutations.stream()
                .filter(mutation -> mutation.getTransactionType().equals(transactionType))
                .filter(mutation -> mutation.getMutationType().equals(mutationType))
                .count());
    }

    private List<AccountMutationSummaryResponseDto.MutationDetail> extractMutations(List<Mutation> mutations,
                                                                                    TransactionType transactionType, MutationType mutationType, User user) {
        return mutations.stream()
                .filter(mutation -> mutation.getTransactionType().equals(transactionType))
                .filter(mutation -> mutation.getMutationType().equals(mutationType))
                .map(mutation -> AccountMutationSummaryResponseDto.MutationDetail.builder()
                        .fullName(mutation.getFullName())
                        .accountNumber(mutation.getAccountNumber())
                        .amount(Formatter.formatToString(mutation.getAmount()))
                        .description(mutation.getDescription())
                        .createdAt(mutation.getCreatedAt())
                        .transactionPurpose(mutation.getTransactionPurpose())
                        .build())
                .toList()
                .stream()
                .toList();
    }

}
