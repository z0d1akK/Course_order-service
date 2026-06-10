package com.innowise.orderservice.security.principal;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class CurrentUser {

    private UUID userId;

    private String role;

    private String email;
}