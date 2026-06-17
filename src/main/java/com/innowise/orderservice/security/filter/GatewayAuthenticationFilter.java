package com.innowise.orderservice.security.filter;

import com.innowise.orderservice.common.constants.Headers;
import com.innowise.orderservice.security.principal.CurrentUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
public class GatewayAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String userIdHeader = request.getHeader(Headers.USER_ID);
        String roleHeader = request.getHeader(Headers.USER_ROLE);
        String emailHeader = request.getHeader(Headers.USER_LOGIN);

        if (userIdHeader != null && roleHeader != null && emailHeader != null) {

            CurrentUser currentUser = CurrentUser.builder()
                    .userId(UUID.fromString(userIdHeader))
                    .role(roleHeader)
                    .email(emailHeader)
                    .build();

            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(
                            currentUser,
                            null,
                            List.of(new SimpleGrantedAuthority(roleHeader))
                    );

            SecurityContextHolder.getContext()
                    .setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}