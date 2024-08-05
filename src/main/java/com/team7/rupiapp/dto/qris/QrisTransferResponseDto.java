package com.team7.rupiapp.dto.qris;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QrisTransferResponseDto {
    private String transactionId;
    private String merchant;
    private String amount;
    private String description;
}
