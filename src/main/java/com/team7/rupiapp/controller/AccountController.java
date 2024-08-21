package com.team7.rupiapp.controller;

import com.team7.rupiapp.api.AccountApi;
import com.team7.rupiapp.dto.account.AccountDetailResponseDto;
import com.team7.rupiapp.dto.account.AccountMutationsDto;
import com.team7.rupiapp.dto.account.MutationDto;
import com.team7.rupiapp.enums.TransactionType;
import com.team7.rupiapp.service.AccountServiceImpl;
import com.team7.rupiapp.util.ApiResponseUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;
import java.time.LocalDate;

@RestController
@RequestMapping("/account")
public class AccountController implements AccountApi {

    private final AccountServiceImpl accountService;

    public AccountController(AccountServiceImpl accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/detail")
    public ResponseEntity<Object> getAccountDetail(@Valid Principal principal) {
        AccountDetailResponseDto response = accountService.getAccountDetail(principal);
        return ApiResponseUtil.success(HttpStatus.OK, "Account Detail fetched", response);
    }

    @GetMapping("/mutations/summary")
    public ResponseEntity<Object> getAccountMutationSummary(@Valid Principal principal,
            @Min(1900) @Max(2100) Integer year,
            @Min(1) @Max(12) Integer month) {
        Object response = accountService.getAccountMutationSummary(principal, year, month);
        return ApiResponseUtil.success(HttpStatus.OK, "Account Mutation Summary fetched", response);
    }

    // @GetMapping("/mutations")
    // public ResponseEntity<AccountMutationsDto> getMutationsByMonthPageable(Principal principal,
    //         @RequestParam(defaultValue = "0") int page,
    //         @RequestParam(defaultValue = "100") int size,
    //         @RequestParam(required = false) Integer year,
    //         @RequestParam(required = false) Integer month,
    //         @RequestParam(required = false) String transactionPurpose,
    //         @RequestParam(required = false) String transactionType,
    //         @RequestParam(required = false) String mutationType) {
    //     AccountMutationsDto response = accountService.getAccountMutationPageable(principal, page, size, year, month,
    //             transactionPurpose, transactionType, mutationType);
    //     return ResponseEntity.ok(response);
    // }

    @GetMapping("/mutations")
    public ResponseEntity<Object> getMutations(Principal principal,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) TransactionType category,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        MutationDto mutationDto = new MutationDto();
        mutationDto.setStartDate(startDate);
        mutationDto.setEndDate(endDate);
        mutationDto.setCategory(category);
        mutationDto.setSearch(search);

        Object response = accountService.getMutations(principal, mutationDto, page, size);
        return ApiResponseUtil.success(HttpStatus.OK, "Mutations fetched", response);
    }

    @GetMapping("/mutations/{id}")
    public ResponseEntity<Object> getMutationDetails(@PathVariable UUID id, Principal principal) {
        Object responseDto = accountService.getMutationDetails(id, principal);
        return ApiResponseUtil.success(HttpStatus.OK, "Mutation details retrieved", responseDto);
    }

}
