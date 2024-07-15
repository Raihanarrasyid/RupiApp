package com.team7.rupiapp.service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

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

    @Override
    public String[] generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails, jwtExpiration);
    }

    @Override
    public String generateToken(UserDetails userDetails, String refreshToken) {
        return createToken(new HashMap<>(), userDetails, jwtExpiration, accessTokenSecretKey);
    }

    private String[] generateToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
        String accessToken = createToken(extraClaims, userDetails, expiration, accessTokenSecretKey);
        String refreshToken = createToken(extraClaims, userDetails, jwtRefreshExpiration,
                refreshTokenSecretKey);

        return new String[] { accessToken, refreshToken };
    }

    private String createToken(Map<String, Object> claims, UserDetails userDetails, long expiration,
            String secretKey) {
        return Jwts.builder()
                .setClaims(claims)
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

        final String username = extractClaim(token, Claims::getSubject, secretKey);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token, secretKey);
    }

    private boolean isTokenExpired(String token, String secretKey) {
        return extractClaim(token, Claims::getExpiration, secretKey).before(new Date());
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

}
