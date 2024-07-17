package com.team7.rupiapp.service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.team7.rupiapp.model.Token;
import com.team7.rupiapp.repository.TokenRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtServiceImpl implements JwtService {
    @Value("${spring.security.jwt.secret-key}")
    private String accessTokenSecretKey;

    @Value("${spring.security.jwt.expiration-time}")
    private long jwtExpiration;

    @Value("${spring.security.jwt.refresh-token.secret-key}")
    private String refreshTokenSecretKey;

    @Value("${spring.security.jwt.refresh-token.expiration-time}")
    private long jwtRefreshExpiration;

    private TokenRepository tokenRepository;

    public JwtServiceImpl(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Override
    public String[] generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails, jwtExpiration);
    }

    @Override
    public String generateToken(UserDetails userDetails, String refreshToken) {
        UUID tokenId = UUID.randomUUID();
        Token token = tokenRepository.findByRefreshTokenId(extractRefreshTokenId(refreshToken));

        token.setTokenId(tokenId);
        tokenRepository.save(token);

        return createToken(new HashMap<>(), tokenId, userDetails, jwtExpiration, accessTokenSecretKey);
    }

    private String[] generateToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
        Token token = tokenRepository.save(new Token());
        UUID tokenId = token.getTokenId();
        UUID refreshTokenId = token.getRefreshTokenId();

        String accessToken = createToken(extraClaims, tokenId, userDetails, expiration, accessTokenSecretKey);
        String refreshToken = createToken(extraClaims, refreshTokenId, userDetails, jwtRefreshExpiration,
                refreshTokenSecretKey);

        return new String[] { accessToken, refreshToken };
    }

    private String createToken(Map<String, Object> claims, UUID tokenId, UserDetails userDetails, long expiration,
            String secretKey) {
        return Jwts.builder()
                .setClaims(claims)
                .setId(tokenId.toString())
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(secretKey), SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        return isTokenValid(token, userDetails, true);
    }

    @Override
    public boolean isRefreshTokenValid(String token, UserDetails userDetails) {
        return isTokenValid(token, userDetails, false);
    }

    private boolean isTokenValid(String token, UserDetails userDetails, boolean isAccessToken) {
        String secretKey = isAccessToken ? accessTokenSecretKey : refreshTokenSecretKey;
        boolean exists = isAccessToken
                ? tokenRepository.existsByTokenId(UUID.fromString(extractClaim(token, Claims::getId, secretKey)))
                : tokenRepository
                        .existsByRefreshTokenId(UUID.fromString(extractClaim(token, Claims::getId, secretKey)));

        final String username = extractClaim(token, Claims::getSubject, secretKey);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token, secretKey) && exists;
    }

    private boolean isTokenExpired(String token, String secretKey) {
        return extractClaim(token, Claims::getExpiration, secretKey).before(new Date());
    }

    private UUID extractTokenId(String token) {
        return UUID.fromString(extractClaim(token, Claims::getId, accessTokenSecretKey));
    }

    private UUID extractRefreshTokenId(String token) {
        return UUID.fromString(extractClaim(token, Claims::getId, refreshTokenSecretKey));
    }

    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject, accessTokenSecretKey);
    }

    @Override
    public String extractRefreshUsername(String token) {
        return extractClaim(token, Claims::getSubject, refreshTokenSecretKey);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver, String secretKey) {
        final Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSignInKey(secretKey))
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claimsResolver.apply(claims);
    }

    private Key getSignInKey(String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public boolean isTokenEnabled(String token) {
        UUID tokenId = extractTokenId(token);
        Token tokenEntity = tokenRepository.findByTokenId(tokenId).orElseThrow();

        return tokenEntity.isEnabled();
    }

    @Override
    public void verifyToken(String token) {
        UUID tokenId = extractTokenId(token);
        Token tokenEntity = tokenRepository.findByTokenId(tokenId).orElseThrow();

        tokenEntity.setEnabled(true);
        tokenRepository.save(tokenEntity);
    }

    @Override
    public void signOut(String token) {
        markTokenAsCanNotBeUsed(token, true);
    }

    private void markTokenAsCanNotBeUsed(String token, boolean isAccessToken) {
        UUID tokenId = UUID.fromString(
                extractClaim(token, Claims::getId, isAccessToken ? accessTokenSecretKey : refreshTokenSecretKey));
        if (isAccessToken) {
            tokenRepository.deleteByTokenId(tokenId);
        } else {
            tokenRepository.deleteByRefreshTokenId(tokenId);
        }
    }
}
