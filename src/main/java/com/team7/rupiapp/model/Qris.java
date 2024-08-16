package com.team7.rupiapp.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.team7.rupiapp.enums.QrisType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "qris")
public class Qris {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private QrisType type;

    @Column(name = "transaction_id", nullable = false)
    private String transactionId;

    private String payload;

    @Column(nullable = false)
    private boolean used = false;

    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expiredAt;
}
