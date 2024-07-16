package com.team7.rupiapp.service;

import com.team7.rupiapp.dto.destination.DestinationDto;
import com.team7.rupiapp.dto.transfer.TransferRequestDto;
import com.team7.rupiapp.dto.transfer.TransferResponseDto;

import java.security.Principal;
import java.util.List;

public interface TransactionService {
    TransferResponseDto createTransaction(TransferRequestDto requestDto);

    List<DestinationDto> getDestination(Principal principal);

    void addFavorites(DestinationDto destinationDto, Principal principal);
}

