package com.team7.rupiapp.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.team7.rupiapp.model.Token;

import jakarta.transaction.Transactional;

@Repository
@Transactional
public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByTokenId(UUID tokenId);

    Token findByRefreshTokenId(UUID refreshTokenId);

    boolean existsByTokenId(UUID tokenId);

    boolean existsByRefreshTokenId(UUID refreshTokenId);

    void deleteByTokenId(UUID tokenId);

    void deleteByRefreshTokenId(UUID refreshTokenId);
}
