package com.innowise.orderservice.common;

import com.innowise.orderservice.common.annotation.WithMockCustomUser;
import com.innowise.orderservice.security.principal.CurrentUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.List;
import java.util.UUID;

public class WithMockCustomUserSecurityContextFactory
        implements WithSecurityContextFactory<WithMockCustomUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser customUser) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        CurrentUser principal = CurrentUser.builder()
                .userId(UUID.fromString(customUser.userId()))
                .email(customUser.email())
                .role(customUser.role())
                .build();

        Authentication auth = new UsernamePasswordAuthenticationToken(
                principal,
                null,
                List.of(new SimpleGrantedAuthority(customUser.role()))
        );

        context.setAuthentication(auth);
        return context;
    }
}