package com.innowise.orderservice.order.testclasses;

import com.innowise.orderservice.client.user.dto.UserInfoResponseDto;
import com.innowise.orderservice.item.entity.Item;
import com.innowise.orderservice.order.dto.request.CreateOrderRequestDto;
import com.innowise.orderservice.order.dto.request.OrderItemRequestDto;
import com.innowise.orderservice.order.dto.request.UpdateOrderRequestDto;
import com.innowise.orderservice.order.dto.response.OrderDetailsResponseDto;
import com.innowise.orderservice.order.dto.response.OrderResponseDto;
import com.innowise.orderservice.order.entity.Order;
import com.innowise.orderservice.order.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class OrderTestDataFactory {

    private OrderTestDataFactory() { }

    public static CreateOrderRequestDto createOrderRequest(UUID userId, UUID itemId) {
        return CreateOrderRequestDto.builder()
                .userId(userId)
                .status(OrderStatus.CREATED)
                .items(List.of(
                        OrderItemRequestDto.builder()
                                .itemId(itemId)
                                .quantity(2)
                                .build()
                ))
                .build();
    }

    public static UpdateOrderRequestDto updateOrderRequest(UUID itemId) {
        return UpdateOrderRequestDto.builder()
                .status(OrderStatus.COMPLETED)
                .items(List.of(
                        OrderItemRequestDto.builder()
                                .itemId(itemId)
                                .quantity(3)
                                .build()
                ))
                .build();
    }

    public static Item createItem(UUID id) {
        return Item.builder()
                .id(id)
                .name("IPhone 17")
                .price(BigDecimal.valueOf(3000))
                .build();
    }

    public static Order createOrder(UUID orderId, UUID userId) {
        return Order.builder()
                .id(orderId)
                .userId(userId)
                .status(OrderStatus.CREATED)
                .orderItems(new ArrayList<>())
                .deleted(false)
                .totalPrice(BigDecimal.valueOf(6000))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static Order createDeletedOrder(UUID orderId, UUID userId) {
        Order order = createOrder(orderId, userId);
        order.setDeleted(true);

        return order;
    }

    public static UserInfoResponseDto createUserResponse(UUID userId) {
        return UserInfoResponseDto.builder()
                .id(userId)
                .name("test")
                .surname("test")
                .email("test@gmail.com")
                .active(true)
                .build();
    }

    public static OrderResponseDto createOrderResponse(UUID orderId, UUID userId) {
        return OrderResponseDto.builder()
                .id(orderId)
                .userId(userId)
                .status(OrderStatus.CREATED)
                .totalPrice(BigDecimal.valueOf(6000))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static OrderDetailsResponseDto createOrderDetailsResponse(UUID orderId, UUID userId) {
        return OrderDetailsResponseDto.builder()
                .order(createOrderResponse(orderId, userId))
                .user(createUserResponse(userId))
                .build();
    }
}