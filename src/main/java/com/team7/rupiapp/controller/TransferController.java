package com.team7.rupiapp.controller;

import com.team7.rupiapp.api.TransferApi;
import com.team7.rupiapp.dto.destination.DestinationAddDto;
import com.team7.rupiapp.dto.destination.DestinationDetailDto;
import com.team7.rupiapp.dto.destination.DestinationDto;
import com.team7.rupiapp.dto.destination.DestinationFavoriteDto;
import com.team7.rupiapp.dto.qris.QrisDto;
import com.team7.rupiapp.dto.qris.QrisResponseDto;
import com.team7.rupiapp.dto.qris.QrisTransferResponseDto;
import com.team7.rupiapp.dto.transfer.TransferRequestDto;
import com.team7.rupiapp.dto.transfer.TransferResponseDto;
import com.team7.rupiapp.service.TransactionService;
import com.team7.rupiapp.util.ApiResponseUtil;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/transfer")
public class TransferController implements TransferApi {
    private final TransactionService transactionService;

    public TransferController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/intrabank")
    public ResponseEntity<Object> transferIntrabank(@Valid @RequestBody TransferRequestDto requestDto, Principal principal) {
        TransferResponseDto responseDto = transactionService.createTransaction(requestDto, principal);
        return ApiResponseUtil.success(HttpStatus.OK, "Transfer success", responseDto);
    }

    @GetMapping("/destinations")
    public ResponseEntity<Object> getDestinations(@Valid Principal principal) {
        List<DestinationDto> responseDestinations = transactionService.getDestination(principal);
        return ApiResponseUtil.success(HttpStatus.OK, "List of destinations", responseDestinations);
    }

    @PostMapping("/destinations")
    public ResponseEntity<Object> addDestination(@Valid @RequestBody DestinationAddDto requestDto, Principal principal) {
        DestinationAddDto responseDto = transactionService.addDestination(requestDto, principal);
        return ApiResponseUtil.success(HttpStatus.OK,"transaction has been added",responseDto);
    }

    @PatchMapping("/destinations/{id}")
    public ResponseEntity<Object> addFavorites(@PathVariable("id") UUID id,@Valid @RequestBody DestinationFavoriteDto requestDto){
        transactionService.addFavorites(id,requestDto);
        return ApiResponseUtil.success(HttpStatus.OK,"transaction added to favorites");
    }

    @GetMapping("/destinations/{id}")
    public ResponseEntity<Object> getDetail(@PathVariable("id") UUID id){
        DestinationDetailDto destinationDetail = transactionService.getDestinationDetail(id);
        return ApiResponseUtil.success(HttpStatus.OK,"transaction detail has been sent", destinationDetail);
    }

    @GetMapping("/qris/{data}")
    public ResponseEntity<Object> getDetailQris(@PathVariable("data") String qris){
        QrisResponseDto qrisResponse = transactionService.detailQris(qris);
        return ApiResponseUtil.success(HttpStatus.OK,"Qris detail has been sent", qrisResponse);
    }

    @PostMapping("/qris")
    public ResponseEntity<Object> createTransactionQris(@Valid @RequestBody QrisDto qrisDto, Principal principal){
        QrisTransferResponseDto responseDto = transactionService.createTransactionQris(principal, qrisDto);
        return ApiResponseUtil.success(HttpStatus.OK, "Qris transaction has been created", responseDto);
    }
}