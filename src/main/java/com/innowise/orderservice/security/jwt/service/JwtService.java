package com.innowise.orderservice.security.jwt.service;

import com.innowise.orderservice.security.jwt.JwtClaims;

public interface JwtService {

    boolean isTokenValid(String token);

    JwtClaims extractClaims(String token);
}