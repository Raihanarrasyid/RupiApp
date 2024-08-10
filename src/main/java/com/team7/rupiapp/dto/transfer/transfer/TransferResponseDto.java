package com.team7.rupiapp.dto.transfer.transfer;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransferResponseDto {
    private ReceiverDetail receiverDetail;
    private MutationDetail mutationDetail;
    private SenderDetail senderDetail;
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

