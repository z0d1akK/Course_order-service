package com.innowise.orderservice.security.util;

import com.innowise.orderservice.security.principal.CurrentUser;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

public final class SecurityUtils {

    private SecurityUtils() { }

    public static CurrentUser getCurrentUser() {
        return (CurrentUser) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
    }

    public static UUID getCurrentUserId() {
        return getCurrentUser().getUserId();
    }

    public static boolean isAdmin() {
        return "ROLE_ADMIN".equals(getCurrentUser().getRole());
    }
}