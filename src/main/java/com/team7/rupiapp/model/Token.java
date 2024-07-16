package com.team7.rupiapp.model;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
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
@Table(name = "tokens")
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "token_id", nullable = false, unique = true)
    private UUID tokenId;

    @Column(name = "refreshToken_id", nullable = false, unique = true)
    private UUID refreshTokenId;

    @Column(nullable = false)
    private boolean enabled = false;

    @PrePersist
    public void prePersist() {
        if (tokenId == null) {
            tokenId = UUID.randomUUID();
        }

        if (refreshTokenId == null) {
            refreshTokenId = UUID.randomUUID();
        }
    }
}
