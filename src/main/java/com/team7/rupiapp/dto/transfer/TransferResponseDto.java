package com.team7.rupiapp.dto.transfer;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TransferResponseDto {
    private ReceiverDetail destinationDetail;
    private MutationDetail mutationDetail;
    private SenderDetail userDetail;
    private String description;
    private String transactionPurpose;

    @Data
    public static class ReceiverDetail {
        private String name;
        private String accountNumber;
    }

    @Data
    public static class MutationDetail {
        private Double amount;
        private LocalDateTime createdAt;
    }

    @Data
    public static class SenderDetail {
        private String name;
        private String accountNumber;
    }
}

