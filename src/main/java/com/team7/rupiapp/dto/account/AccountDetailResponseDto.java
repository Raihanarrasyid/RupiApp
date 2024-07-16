package com.team7.rupiapp.dto.account;

import lombok.Data;

@Data
public class AccountDetailResponseDto {
    private String fullName;
    private String email;
    private String accountNumber;
    private Double balance;
}
