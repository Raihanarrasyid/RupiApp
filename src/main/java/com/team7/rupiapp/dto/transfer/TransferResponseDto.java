package com.team7.rupiapp.dto.transfer;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TransferResponseDto {
    private String receiverName;
    private String receiverBankName;
    private String receiverAccountNumber;

    private Double amount;
    private LocalDateTime createdAt;

    private String senderName;
    private String senderAccountNumber;
}

