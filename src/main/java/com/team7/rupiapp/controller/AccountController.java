package com.team7.rupiapp.controller;


import com.team7.rupiapp.api.AccountApi;
import com.team7.rupiapp.dto.account.AccountDetailResponseDto;
import com.team7.rupiapp.dto.account.AccountMutationsMonthlyDto;
import com.team7.rupiapp.service.AccountServiceImpl;
import com.team7.rupiapp.util.ApiResponseUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

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

    @GetMapping("/mutations")
    public ResponseEntity<Object> getAccountMutation(@Valid Principal principal) {
        Object response = accountService.getAccountMutation(principal);
        return ApiResponseUtil.success(HttpStatus.OK, "Account Mutations fetched", response);
    }

    @GetMapping("/mutations/page")
    public ResponseEntity<AccountMutationsMonthlyDto> getMutationsByMonthPageable(Principal principal,
                                                                                  @RequestParam(defaultValue = "0") int page,
                                                                                  @RequestParam(defaultValue = "10") int size) {
        AccountMutationsMonthlyDto response = accountService.getAccountMutationPageable(principal, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/mutations/page/filter")
    public ResponseEntity<AccountMutationsMonthlyDto> getMutationsByMonthPageable(Principal principal,
                                                                                  @RequestParam(defaultValue = "0") int page,
                                                                                  @RequestParam(defaultValue = "10") int size,
                                                                                  @RequestParam(required = false) Integer year,
                                                                                  @RequestParam(required = false) Integer month,
                                                                                  @RequestParam(required = false) String transactionPurpose,
                                                                                  @RequestParam(required = false) String transactionType,
                                                                                  @RequestParam(required = false) String mutationType
    ) {
        AccountMutationsMonthlyDto response = accountService.getAccountMutationPageable(principal, page, size, year, month, transactionPurpose, transactionType, mutationType);
        return ResponseEntity.ok(response);
    }

}
