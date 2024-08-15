package com.team7.rupiapp.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.team7.rupiapp.model.Qris;

public interface QrisRepository extends JpaRepository<Qris, UUID> {
    Qris findByTransactionId(String transactionId);

    Qris findByPayload(String payload);
}
