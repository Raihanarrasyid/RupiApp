package com.team7.rupiapp.service;

import com.team7.rupiapp.dto.transfer.TransferRequestDto;
import com.team7.rupiapp.dto.transfer.TransferResponseDto;

public interface TransactionService {
    TransferResponseDto createTransaction(TransferRequestDto requestDto);
}

