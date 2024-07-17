package com.team7.rupiapp.dto.transfer;

import com.team7.rupiapp.enums.MutationType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TransferResponseDto {
    private UUID id;
    private UUID userId;
    private UUID destinationId;
    private Double amount;
    private String description;
    private LocalDateTime createdAt;
    private MutationType type;
}
