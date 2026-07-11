package com.innowise.orderservice.security.filter;

import com.innowise.orderservice.common.constants.Headers;
import com.innowise.orderservice.common.exception.ErrorMessages;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class GatewayApiKeyFilter extends OncePerRequestFilter {

    @Value("${gateway.api-key}")
    private String gatewayApiKey;

    private static final String[] EXCLUDED_PATHS = {
            "/api-docs",
            "/v3/api-docs",
            "/swagger-ui",
            "/swagger-ui.html",
            "/actuator/health",
            "/actuator/info",
            "/internal",
            "/error"
    };

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

        for (String excludedPath : EXCLUDED_PATHS) {
            if (path.startsWith(excludedPath)) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        String gatewayHeader = request.getHeader(Headers.GATEWAY_KEY);

        if (gatewayHeader == null || !gatewayApiKey.equals(gatewayHeader)) {
            response.sendError(
                    HttpStatus.UNAUTHORIZED.value(),
                    ErrorMessages.INVALID_GATEWAY_KEY
            );
            return;
        }

        filterChain.doFilter(request, response);
    }
}