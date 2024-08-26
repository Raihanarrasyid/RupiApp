package com.team7.rupiapp.service;

import com.team7.rupiapp.dto.account.*;
import com.team7.rupiapp.dto.transfer.qris.QrisTransferResponseDto;
import com.team7.rupiapp.dto.transfer.transfer.TransferResponseDto;
import com.team7.rupiapp.enums.MutationType;
import com.team7.rupiapp.enums.TransactionType;
import com.team7.rupiapp.exception.BadRequestException;
import com.team7.rupiapp.exception.DataNotFoundException;
import com.team7.rupiapp.exception.UnauthorizedException;
import com.team7.rupiapp.model.Mutation;
import com.team7.rupiapp.model.User;
import com.team7.rupiapp.repository.MutationRepository;
import com.team7.rupiapp.repository.UserRepository;
import com.team7.rupiapp.util.Formatter;
import jakarta.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl implements AccountService {
        private final ModelMapper modelMapper;
        private final UserRepository userRepository;
        private final MutationRepository mutationRepository;

        public AccountServiceImpl(ModelMapper modelMapper, UserRepository userRepository,
                        MutationRepository mutationRepository) {
                this.modelMapper = modelMapper;
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
                 * By default, the rangeStartMutationDate defined as the beginning of the month
                 * of the given year and month.
                 * If the 'year' and 'month' fields are provided, the rangeStartMutationDate
                 * will be
                 * set based on the respective fields.
                 * The date of month cannot be set to a future date. Reason: the data is not
                 * available yet.
                 *
                 * rangeEndMutationDate:
                 * By default, the rangeEndMutationDate defined as the end of the month.
                 * The date of month defined as the last day of the month or current date if
                 * the provided 'month' parameter is equals to current month.
                 */
                LocalDateTime rangeStartMutationDate = getRangeStartMutationLocalDateTime(year, month);
                LocalDateTime rangeEndMutationDate = getRangeEndMutationLocalDateTime(rangeStartMutationDate);

                User foundUser = userRepository.findByUsername(principal.getName())
                                .orElseThrow(() -> new DataNotFoundException("User not found"));

                List<Mutation> mutations = mutationRepository.findByUserIdAndCreatedAtBetween(
                                foundUser.getId(), rangeStartMutationDate, rangeEndMutationDate);

                /*
                 * The balance information (expense and income) calculations are explained as
                 * follows:
                 * totalIncome = 100
                 * totalExpense = 50
                 * denominatorBalance = totalIncome + totalExpense = 100 + 50 = 150 // In
                 * percentage: 100%
                 * totalIncomePercentage = (totalIncome / denominatorBalance) * 100 = (100 /
                 * 150) * 100 = 66.67%
                 * totalExpensePercentage = (totalExpense / denominatorBalance) * 100 = (50 /
                 * 150) * 100 = 33.33%
                 * totalEarnings = totalIncome - totalExpense = 100 - 50 = 50
                 *
                 * The category details are explained as follows:
                 * numberOfTransactions = 2 // fetched from the number of transactions in the
                 * category
                 * totalBalance = 100 // fetched from the total balance in the category
                 * totalBalancePercentage = (totalBalance /
                 * totalExpenseOrIncomeAsDenominatorBalance) * 100 // total
                 * balance percentage by TransactionType
                 */
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
                                                .totalIncomePercentage(calculateBalancePercentage(totalIncome,
                                                                denominatorBalance))
                                                .build())
                                .expense(AccountMutationSummaryResponseDto.ExpenseDetail.builder()
                                                .categories(debitCategories)
                                                .totalExpense(Formatter.formatToString(totalExpense))
                                                .totalExpensePercentage(calculateBalancePercentage(totalExpense,
                                                                denominatorBalance))
                                                .build())
                                .totalEarnings(Formatter.formatToString(totalEarnings))
                                .rangeStartMutationDate(rangeStartMutationDate)
                                .rangeEndMutationDate(rangeEndMutationDate)
                                .build();
        }

        private static LocalDateTime getRangeStartMutationLocalDateTime(Integer year, Integer month) {
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime rangeStartMutationDate;

                if (year == null && month == null) {
                        rangeStartMutationDate = LocalDateTime.of(now.getYear(), now.getMonth(), 1, 0, 0);
                } else if (year == null || month == null) {
                        throw new BadRequestException(
                                        "Both 'year' and 'month' parameters must be provided, or none at all.");
                } else {
                        rangeStartMutationDate = LocalDateTime.of(year, month, 1, 0, 0);
                }

                return rangeStartMutationDate;
        }

        private static LocalDateTime getRangeEndMutationLocalDateTime(LocalDateTime rangeStartMutationDate) {
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime rangeEndMutationDate;

                if (rangeStartMutationDate.getYear() == now.getYear()
                                && rangeStartMutationDate.getMonth() == now.getMonth()) {
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

        private List<AccountMutationSummaryResponseDto.CategoryDetail> getCategoryDetails(List<Mutation> mutations,
                        TransactionType transactionType, double denominatorBalance, User user) {
                return mutations.stream()
                                .filter(mutation -> mutation.getTransactionType().equals(transactionType))
                                .collect(Collectors.groupingBy(
                                                Mutation::getMutationType,
                                                Collectors.summingDouble(Mutation::getAmount)))
                                .entrySet().stream()
                                .map(mutationsGroupedByMutationTypeSummedByMutationAmount -> AccountMutationSummaryResponseDto.CategoryDetail
                                                .builder()
                                                .type(mutationsGroupedByMutationTypeSummedByMutationAmount.getKey())
                                                .numberOfTransactions(
                                                                extractNumberOfTransaction(mutations, transactionType,
                                                                                mutationsGroupedByMutationTypeSummedByMutationAmount
                                                                                                .getKey()))
                                                .totalBalance(Formatter
                                                                .formatToString(mutationsGroupedByMutationTypeSummedByMutationAmount
                                                                                .getValue()))
                                                .totalBalancePercentage(calculateBalancePercentage(
                                                                mutationsGroupedByMutationTypeSummedByMutationAmount
                                                                                .getValue(),
                                                                denominatorBalance))
                                                .mutations(
                                                                extractMutations(mutations, transactionType,
                                                                                mutationsGroupedByMutationTypeSummedByMutationAmount
                                                                                                .getKey()))
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
                        TransactionType transactionType, MutationType mutationType) {
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

        @Override
        @Transactional
        public Object getMutationDetails(UUID mutationId, Principal principal) {
                Mutation mutation = mutationRepository.findById(mutationId)
                                .orElseThrow(() -> new DataNotFoundException("Mutation not found"));

                User user = userRepository.findByUsername(principal.getName())
                                .orElseThrow(() -> new DataNotFoundException("User not found"));

                if (!mutation.getUser().getId().equals(user.getId())) {
                        throw new UnauthorizedException("User is not authorized to access this mutation");
                }

                if (mutation.getMutationType() == MutationType.QRIS && mutation.getAccountNumber() == null) {
                        return buildQrisResponseDto(mutation);
                } else if (mutation.getMutationType() == MutationType.QRIS
                                || mutation.getMutationType() == MutationType.QR) {
                        return buildTransferResponseDto(mutation);
                } else if (mutation.getMutationType() == MutationType.TRANSFER) {
                        return buildTransferResponseDto(mutation);
                } else {
                        throw new DataNotFoundException("Invalid mutation type");
                }
        }

        private QrisTransferResponseDto buildQrisResponseDto(Mutation mutation) {
                QrisTransferResponseDto qrisResponseDto = new QrisTransferResponseDto();
                qrisResponseDto.setMutationId(mutation.getId().toString());
                qrisResponseDto.setMerchant(mutation.getFullName());
                qrisResponseDto.setAmount(String.valueOf(mutation.getAmount()));
                qrisResponseDto.setDescription(mutation.getDescription());
                return qrisResponseDto;
        }

        private TransferResponseDto buildTransferResponseDto(Mutation mutation) {
                User sender = mutation.getUser();
                TransferResponseDto transferResponseDto = new TransferResponseDto();

                TransferResponseDto.SenderDetail senderDetail = new TransferResponseDto.SenderDetail();
                TransferResponseDto.ReceiverDetail receiverDetail = new TransferResponseDto.ReceiverDetail();

                if (mutation.getTransactionType() == TransactionType.DEBIT) {
                        senderDetail.setName(sender.getFullName());
                        senderDetail.setAccountNumber(sender.getAccountNumber());
                        receiverDetail.setName(mutation.getFullName());
                        receiverDetail.setAccountNumber(mutation.getAccountNumber());
                } else if (mutation.getTransactionType() == TransactionType.CREDIT) {
                        senderDetail.setName(mutation.getFullName());
                        senderDetail.setAccountNumber(mutation.getAccountNumber());
                        receiverDetail.setName(sender.getFullName());
                        receiverDetail.setAccountNumber(sender.getAccountNumber());
                }

                transferResponseDto.setSenderDetail(senderDetail);
                transferResponseDto.setReceiverDetail(receiverDetail);

                TransferResponseDto.MutationDetail mutationDetail = new TransferResponseDto.MutationDetail();
                mutationDetail.setAmount(mutation.getAmount());
                mutationDetail.setCreatedAt(mutation.getCreatedAt());
                mutationDetail.setMutationId(mutation.getId());
                transferResponseDto.setMutationDetail(mutationDetail);

                transferResponseDto.setDescription(mutation.getDescription());
                transferResponseDto.setTransactionPurpose(mutation.getTransactionPurpose().toString());

                return transferResponseDto;
        }

        @Override
        public Page<MutationResponseDto> getMutations(Principal principal, MutationDto mutationDto, int page,
                        int size) {
                User user = findUserByPrincipal(principal);

                validateDateRange(mutationDto);

                List<Mutation> filteredMutations = filterMutations(user.getMutations(), mutationDto);

                List<MutationResponseDto> mutationDtos = mapToMutationResponseDtos(filteredMutations, page, size);

                return new PageImpl<>(mutationDtos, PageRequest.of(page, size), filteredMutations.size());
        }

        private User findUserByPrincipal(Principal principal) {
                return userRepository.findByUsername(principal.getName())
                                .orElseThrow(() -> new DataNotFoundException("User not found"));
        }

        private void validateDateRange(MutationDto mutationDto) {
                if (mutationDto.getStartDate() != null && mutationDto.getEndDate() != null
                                && mutationDto.getStartDate().isAfter(mutationDto.getEndDate())) {
                        throw new BadRequestException("Start date cannot be greater than end date");
                }
        }

        private List<Mutation> filterMutations(List<Mutation> mutations, MutationDto mutationDto) {
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm 'WIB'");

                return mutations.stream()
                                .sorted(Comparator.comparing(Mutation::getCreatedAt).reversed())
                                .filter(mutation -> isWithinDateRange(mutation, mutationDto))
                                .filter(mutation -> matchesCategory(mutation, mutationDto))
                                .filter(mutation -> matchesSearchCriteria(mutation, mutationDto, dateFormatter,
                                                timeFormatter))
                                .collect(Collectors.toList());
        }

        private boolean isWithinDateRange(Mutation mutation, MutationDto mutationDto) {
                return (mutationDto.getStartDate() == null
                                || !mutation.getCreatedAt().toLocalDate().isBefore(mutationDto.getStartDate()))
                                && (mutationDto.getEndDate() == null || !mutation.getCreatedAt().toLocalDate()
                                                .isAfter(mutationDto.getEndDate()));
        }

        private boolean matchesCategory(Mutation mutation, MutationDto mutationDto) {
                return mutationDto.getCategory() == null
                                || mutation.getTransactionType().equals(mutationDto.getCategory());
        }

        private boolean matchesSearchCriteria(Mutation mutation, MutationDto mutationDto,
                        DateTimeFormatter dateFormatter, DateTimeFormatter timeFormatter) {
                if (mutationDto.getSearch() == null) {
                        return true;
                }

                String search = mutationDto.getSearch().toLowerCase();

                return (mutation.getFullName() != null && mutation.getFullName().toLowerCase().contains(search))
                                || (mutation.getAmount() != null
                                                && String.valueOf(mutation.getAmount()).contains(search))
                                || (mutation.getAccountNumber() != null && mutation.getAccountNumber().contains(search))
                                || (mutation.getCreatedAt() != null && mutation.getCreatedAt().toLocalDate()
                                                .format(dateFormatter).contains(search))
                                || (mutation.getCreatedAt() != null && mutation.getCreatedAt().toLocalTime()
                                                .format(timeFormatter).contains(search))
                                || (mutation.getDescription() != null
                                                && mutation.getDescription().toLowerCase().contains(search))
                                || (mutation.getMutationType() != null && mutation.getMutationType().toString()
                                                .toLowerCase().contains(search));
        }

        private List<MutationResponseDto> mapToMutationResponseDtos(List<Mutation> filteredMutations, int page,
                        int size) {
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm 'WIB'");
                int start = page * size;
                int end = Math.min(start + size, filteredMutations.size());

                List<Mutation> paginatedMutations = start < filteredMutations.size()
                                ? filteredMutations.subList(start, end)
                                : Collections.emptyList();

                return paginatedMutations.stream()
                                .map(mutation -> {
                                        MutationResponseDto dto = modelMapper.map(mutation, MutationResponseDto.class);
                                        dto.setDate(mutation.getCreatedAt().toLocalDate());
                                        dto.setTime(mutation.getCreatedAt().toLocalTime().format(timeFormatter));

                                        if (mutation.getMutationType() == MutationType.TRANSFER
                                                        || mutation.getMutationType() == MutationType.QR) {
                                                dto.setBankName("Rupi App");
                                        } else {
                                                dto.setBankName(null);
                                        }
                                        return dto;
                                })
                                .collect(Collectors.toList());
        }

}
