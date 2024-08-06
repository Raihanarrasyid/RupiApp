package com.team7.rupiapp.service;

import com.team7.rupiapp.dto.destination.DestinationAddDto;
import com.team7.rupiapp.dto.destination.DestinationDetailDto;
import com.team7.rupiapp.dto.destination.DestinationDto;
import com.team7.rupiapp.dto.destination.DestinationFavoriteDto;
import com.team7.rupiapp.dto.qris.QrisDto;
import com.team7.rupiapp.dto.qris.QrisResponseDto;
import com.team7.rupiapp.dto.qris.QrisTransferResponseDto;
import com.team7.rupiapp.dto.transfer.TransferRequestDto;
import com.team7.rupiapp.dto.transfer.TransferResponseDto;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

public interface TransactionService {
    public TransferResponseDto createTransaction(TransferRequestDto requestDto, Principal principal);

    public List<DestinationDto> getDestination(Principal principal);

    public void addFavorites(UUID id, DestinationFavoriteDto destinationFavoriteDto);

    public DestinationAddDto addDestination(DestinationAddDto requestDto, Principal principal);

    public DestinationDetailDto getDestinationDetail(UUID id);

    public QrisResponseDto detailQris(String qris);

    public QrisTransferResponseDto createTransactionQris(Principal principal, QrisDto qrisDto);

    Object getTransactionDetails(UUID transactionId);
}

