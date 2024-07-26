package com.team7.rupiapp.controller;


import com.team7.rupiapp.api.AccountApi;
import com.team7.rupiapp.dto.account.AccountDetailResponseDto;
import com.team7.rupiapp.service.AccountServiceImpl;
import com.team7.rupiapp.util.ApiResponseUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public ResponseEntity<Object> getAccountMutationSummary(@Valid Principal principal) {
        Object response = accountService.getAccountMutationSummary(principal);
        return ApiResponseUtil.success(HttpStatus.OK, "Account Mutation Summary fetched", response);
    }

    @GetMapping("/mutations")
    public ResponseEntity<Object> getAccountMutation(@Valid Principal principal) {
        Object response = accountService.getAccountMutation(principal);
        return ApiResponseUtil.success(HttpStatus.OK, "Account Mutations fetched", response);
    }

}
