package com.innowise.orderservice.security.jwt.service.impl;

import com.innowise.orderservice.security.jwt.JwtClaims;
import com.innowise.orderservice.security.jwt.JwtProperties;
import com.innowise.orderservice.security.jwt.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    private final JwtProperties jwtProperties;

    @Override
    public boolean isTokenValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public JwtClaims extractClaims(String token) {

        Claims claims = parseClaims(token);

        return JwtClaims.builder()
                .userId(UUID.fromString(claims.getSubject()))
                .role(claims.get("role", String.class))
                .email(claims.get("email", String.class))
                .build();
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }
}