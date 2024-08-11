package com.team7.rupiapp.dto.transfer.qris;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QrisResponseDto {
    private String type;
    private String transactionId;
    private String merchant;
    private String amount;
}
