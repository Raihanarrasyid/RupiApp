package com.team7.rupiapp.service;

import com.team7.rupiapp.dto.account.AccountDetailResponseDto;
import com.team7.rupiapp.exception.DataNotFoundException;
import com.team7.rupiapp.model.User;
import com.team7.rupiapp.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {
    private final UserRepository userRepository;

    public AccountServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public AccountDetailResponseDto getAccountDetail(Principal principal) {
        try {
            // Validate user
            User foundUser = userRepository.findByUsername(principal.getName())
                    .orElseThrow(() -> new DataNotFoundException("User not found"));

            AccountDetailResponseDto response = new AccountDetailResponseDto();
            response.setFullName(foundUser.getFullName());
            response.setEmail(foundUser.getEmail());
            response.setAccountNumber(foundUser.getAccountNumber());
            response.setBalance(foundUser.getBalance());

            return response;
        } catch (Exception e) {
            throw e;
        }
    }
}
