package com.team7.rupiapp.controller;

import com.team7.rupiapp.dto.destination.DestinationDto;
import com.team7.rupiapp.dto.transfer.TransferRequestDto;
import com.team7.rupiapp.dto.transfer.TransferResponseDto;
import com.team7.rupiapp.service.TransactionService;
import com.team7.rupiapp.util.ApiResponseUtil;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/transfer")
public class TransferController {
    private final TransactionService transactionService;

    @Autowired
    public TransferController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/intrabank")
    public ResponseEntity<Object> transferIntrabank(@Valid @RequestBody TransferRequestDto requestDto) {
        TransferResponseDto responseDto = transactionService.createTransaction(requestDto);
        return ApiResponseUtil.success(HttpStatus.OK, "Transfer success", responseDto);
    }

    @GetMapping("/destinations")
    public ResponseEntity<Object> getDestinations(@Valid Principal principal) {
        List<DestinationDto> destinations = transactionService.getDestination(principal);
        return ApiResponseUtil.success(HttpStatus.OK, "List of destinations", destinations);
    }

    @PostMapping("/destination/add")
    public ResponseEntity<Object> addFavorites(@Valid @RequestBody DestinationDto destinationDto, Principal principal){
        transactionService.addFavorites(destinationDto,principal);
        return ApiResponseUtil.success(HttpStatus.OK,"transaction added to favorites");
    }

}