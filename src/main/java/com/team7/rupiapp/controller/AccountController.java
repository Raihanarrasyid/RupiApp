package com.team7.rupiapp.controller;


import com.team7.rupiapp.dto.account.AccountDetailResponseDto;
import com.team7.rupiapp.service.AccountServiceImpl;
import com.team7.rupiapp.service.MutationService;
import com.team7.rupiapp.service.MutationServiceImpl;
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
public class AccountController {

    private final AccountServiceImpl accountService;
    private final MutationServiceImpl mutationService;

    public AccountController(AccountServiceImpl accountService, MutationServiceImpl mutationService) {

        this.accountService = accountService;
        this.mutationService = mutationService;
    }

    @GetMapping("/detail")
    public ResponseEntity<Object> getAccountDetail(@Valid Principal principal) {
        AccountDetailResponseDto response = accountService.getAccountDetail(principal);
        return ApiResponseUtil.success(HttpStatus.OK, "Account detail fetched", response);
    }

    @GetMapping("/mutations")
    public ResponseEntity<Object> getAccountMutation(@Valid Principal principal) {
        Object response = mutationService.getAccountMutation(principal);
        return ApiResponseUtil.success(HttpStatus.OK, "Account Mutations fetched", response);
    }

}
