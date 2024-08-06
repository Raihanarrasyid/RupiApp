package com.team7.rupiapp.dto.transfer;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransferResponseDto {
    private ReceiverDetail destinationDetail;
    private MutationDetail mutationDetail;
    private SenderDetail userDetail;
    private String description;
    private String transactionPurpose;
    private String transactionType;

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

