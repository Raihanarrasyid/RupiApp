package com.team7.rupiapp.service;

import com.team7.rupiapp.dto.transfer.destination.DestinationAddDto;
import com.team7.rupiapp.dto.transfer.destination.DestinationDetailDto;
import com.team7.rupiapp.dto.transfer.destination.DestinationDto;
import com.team7.rupiapp.dto.transfer.destination.DestinationFavoriteDto;
import com.team7.rupiapp.dto.transfer.qris.QrisGenerateCPMDto;
import com.team7.rupiapp.dto.transfer.qris.QrisGenerateMPMDto;
import com.team7.rupiapp.dto.transfer.qris.QrisGenerateResponseDto;
import com.team7.rupiapp.dto.transfer.qris.QrisDto;
import com.team7.rupiapp.dto.transfer.qris.QrisResponseDto;
import com.team7.rupiapp.dto.transfer.qris.QrisTransferResponseDto;
import com.team7.rupiapp.dto.transfer.transfer.TransferRequestDto;
import com.team7.rupiapp.dto.transfer.transfer.TransferResponseDto;

import java.security.Principal;
import java.util.UUID;

import org.springframework.data.domain.Page;

public interface TransactionService {
    public TransferResponseDto createTransaction(TransferRequestDto requestDto, Principal principal);

    public Page<DestinationDto> getDestination(Principal principal, String search, int page, int size);

    public void addFavorites(UUID id, DestinationFavoriteDto destinationFavoriteDto);

    public DestinationAddDto addDestination(DestinationAddDto requestDto, Principal principal);

    public DestinationDetailDto getDestinationDetail(UUID id);

    public QrisResponseDto detailQris(String qris);

    public QrisTransferResponseDto createTransactionQris(Principal principal, QrisDto qrisDto);

    public QrisGenerateResponseDto createQris(Principal principal, QrisGenerateMPMDto qrisMPMDto);

    public QrisGenerateResponseDto createQrisCPM(Principal principal, QrisGenerateCPMDto qrisCPMDto);
}

