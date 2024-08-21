package com.team7.rupiapp.controller;

import com.team7.rupiapp.api.TransferApi;
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
import com.team7.rupiapp.service.TransactionService;
import com.team7.rupiapp.util.ApiResponseUtil;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping("/transfer")
public class TransferController implements TransferApi {
    private final TransactionService transactionService;

    public TransferController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/intrabank")
    public ResponseEntity<Object> transferIntrabank(@Valid @RequestBody TransferRequestDto requestDto,
            Principal principal) {
        TransferResponseDto responseDto = transactionService.createTransaction(requestDto, principal);
        return ApiResponseUtil.success(HttpStatus.OK, "Transfer success", responseDto);
    }

    @GetMapping("/destinations")
    public ResponseEntity<Object> getDestinations(
            @Valid Principal principal,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        Page<DestinationDto> responseDestinations = transactionService.getDestination(principal, search, page, size);
        return ApiResponseUtil.success(HttpStatus.OK, "List of destinations", responseDestinations);
    }

    @PostMapping("/destinations")
    public ResponseEntity<Object> addDestination(@Valid @RequestBody DestinationAddDto requestDto,
            Principal principal) {
        DestinationAddDto responseDto = transactionService.addDestination(requestDto, principal);
        return ApiResponseUtil.success(HttpStatus.OK, "transaction has been added", responseDto);
    }

    @PatchMapping("/destinations/{id}")
    public ResponseEntity<Object> addFavorites(@PathVariable("id") UUID id,
            @Valid @RequestBody DestinationFavoriteDto requestDto) {
        transactionService.addFavorites(id, requestDto);
        String message = requestDto.getIsFavorites() ? "Transaction added to favorites"
                : "Transaction deleted from favorites";

        return ApiResponseUtil.success(HttpStatus.OK, message);
    }

    @GetMapping("/destinations/{id}")
    public ResponseEntity<Object> getDetail(@PathVariable("id") UUID id) {
        DestinationDetailDto destinationDetail = transactionService.getDestinationDetail(id);
        return ApiResponseUtil.success(HttpStatus.OK, "transaction detail has been sent", destinationDetail);
    }

    @GetMapping("/qris/{qris}")
    public ResponseEntity<Object> getDetailQris(@PathVariable("qris") String qris) {
        QrisResponseDto qrisResponse = transactionService.detailQris(qris);
        return ApiResponseUtil.success(HttpStatus.OK, "Qris detail has been sent", qrisResponse);
    }

    @PostMapping("/qris")
    public ResponseEntity<Object> createTransactionQris(@Valid @RequestBody QrisDto qrisDto, Principal principal) {
        QrisTransferResponseDto responseDto = transactionService.createTransactionQris(principal, qrisDto);
        return ApiResponseUtil.success(HttpStatus.OK, "Qris transaction has been created", responseDto);
    }

    @PostMapping("/qris/generate")
    public ResponseEntity<Object> createTransactionQrisMPM(
            @Valid @RequestBody(required = false) QrisGenerateMPMDto qrisDto, Principal principal) {
        QrisGenerateResponseDto responseDto = transactionService.createQris(principal, qrisDto);
        return ApiResponseUtil.success(HttpStatus.OK, "Qris transaction has been created", responseDto);
    }

    @PostMapping("/qris/generate/cpm")
    public ResponseEntity<Object> createTransactionQrisCPM(@Valid @RequestBody QrisGenerateCPMDto qrisDto,
            Principal principal) {
        QrisGenerateResponseDto responseDto = transactionService.createQrisCPM(principal, qrisDto);
        return ApiResponseUtil.success(HttpStatus.OK, "Qris transaction has been created", responseDto);
    }
}