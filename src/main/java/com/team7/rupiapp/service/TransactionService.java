package com.team7.rupiapp.service;

import com.team7.rupiapp.dto.destination.DestinationAddDto;
import com.team7.rupiapp.dto.destination.DestinationDetailDto;
import com.team7.rupiapp.dto.destination.DestinationDto;
import com.team7.rupiapp.dto.destination.DestinationFavoriteDto;
import com.team7.rupiapp.dto.transfer.TransferRequestDto;
import com.team7.rupiapp.dto.transfer.TransferResponseDto;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

public interface TransactionService {
    TransferResponseDto createTransaction(TransferRequestDto requestDto, Principal principal);

    List<DestinationDto> getDestination(Principal principal);

    void addFavorites(UUID id, DestinationFavoriteDto destinationFavoriteDto);

    DestinationAddDto addDestination(DestinationAddDto requestDto, Principal principal);

    DestinationDetailDto getDestinationDetail(UUID id);
}

