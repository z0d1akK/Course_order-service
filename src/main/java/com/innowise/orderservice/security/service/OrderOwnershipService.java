package com.innowise.orderservice.security.service;

import com.innowise.orderservice.order.repository.OrderRepository;
import com.innowise.orderservice.security.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderOwnershipService {

    private final OrderRepository orderRepository;

    public boolean isOwnerOrAdmin(UUID orderId) {
        if (SecurityUtils.isAdmin()) {return true;}

        return orderRepository.existsByIdAndUserId(orderId, SecurityUtils.getCurrentUserId());
    }
}