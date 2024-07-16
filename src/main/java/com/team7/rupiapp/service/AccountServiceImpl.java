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
        Optional<User> foundUser = userRepository.findByUsername(principal.getName());

        AccountDetailResponseDto response = new AccountDetailResponseDto();
        foundUser.ifPresentOrElse(user -> {
            response.setFullName(user.getFullName());
            response.setEmail(user.getEmail());
            response.setAccountNumber(user.getAccountNumber());
            response.setBalance(user.getBalance());
        }, () -> {
            throw new DataNotFoundException("User not found");
        });

        return response;
    }
}
