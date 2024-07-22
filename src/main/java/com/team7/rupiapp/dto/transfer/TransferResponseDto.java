package com.team7.rupiapp.dto.transfer;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TransferResponseDto {
    private Receiver receiver;
    private Mutation mutation;
    private Sender sender;

    @Data
    public static class Receiver {
        private String name;
        private String bankName;
        private String accountNumber;
    }

    @Data
    public static class Mutation {
        private Double amount;
        private LocalDateTime createdAt;
    }

    @Data
    public static class Sender {
        private String name;
        private String accountNumber;
    }
}

