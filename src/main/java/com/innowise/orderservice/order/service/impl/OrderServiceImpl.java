package com.innowise.orderservice.order.service.impl;

import com.innowise.orderservice.client.user.dto.UserInfoResponseDto;
import com.innowise.orderservice.client.user.feign.UserServiceClient;
import com.innowise.orderservice.item.entity.Item;
import com.innowise.orderservice.item.exception.ItemNotFoundException;
import com.innowise.orderservice.item.repository.ItemRepository;
import com.innowise.orderservice.order.dto.request.CreateOrderRequestDto;
import com.innowise.orderservice.order.dto.request.OrderFilterRequestDto;
import com.innowise.orderservice.order.dto.request.OrderItemRequestDto;
import com.innowise.orderservice.order.dto.request.UpdateOrderRequestDto;
import com.innowise.orderservice.order.dto.response.OrderDetailsResponseDto;
import com.innowise.orderservice.order.entity.Order;
import com.innowise.orderservice.order.entity.OrderItem;
import com.innowise.orderservice.order.exception.OrderNotFoundException;
import com.innowise.orderservice.order.mapper.OrderDetailsMapper;
import com.innowise.orderservice.order.mapper.OrderMapper;
import com.innowise.orderservice.order.repository.OrderRepository;
import com.innowise.orderservice.order.repository.specification.OrderSpecification;
import com.innowise.orderservice.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final ItemRepository itemRepository;

    private final OrderMapper orderMapper;

    private final OrderDetailsMapper orderDetailsMapper;

    private final UserServiceClient userServiceClient;

    @Override
    @Transactional
    public OrderDetailsResponseDto create(CreateOrderRequestDto request) {

        Order order = orderMapper.toEntity(request);

        List<OrderItem> orderItems = request.getItems()
                .stream()
                .map(item -> buildOrderItem(item, order))
                .toList();

        order.setOrderItems(orderItems);
        order.setDeleted(false);
        order.setTotalPrice(calculateTotalPrice(orderItems));

        Order savedOrder = orderRepository.save(order);

        return buildResponse(savedOrder);
    }

    @Override
    public OrderDetailsResponseDto getById(UUID id) {
        return buildResponse(getOrderEntity(id));
    }

    @Override
    public Page<OrderDetailsResponseDto> getAll(OrderFilterRequestDto filter, Pageable pageable) {
        return orderRepository.findAll(
                        OrderSpecification.notDeleted()
                                .and(OrderSpecification.createdFrom(filter.getCreatedFrom()))
                                .and(OrderSpecification.createdTo(filter.getCreatedTo()))
                                .and(OrderSpecification.hasStatuses(filter.getStatuses())),
                        pageable
                )
                .map(this::buildResponse);
    }

    @Override
    public Page<OrderDetailsResponseDto> getByUserId(UUID userId, Pageable pageable) {
        return orderRepository.findAllByUserIdAndDeletedFalse(userId, pageable)
                .map(this::buildResponse);
    }

    @Override
    @Transactional
    public OrderDetailsResponseDto update(UUID id, UpdateOrderRequestDto request) {

        Order order = getOrderEntity(id);

        orderMapper.updateEntity(request, order);

        if (request.getItems() != null) {
            order.getOrderItems().clear();

            List<OrderItem> orderItems = request.getItems()
                    .stream()
                    .map(item -> buildOrderItem(item, order))
                    .toList();

            order.getOrderItems().addAll(orderItems);

            order.setTotalPrice(calculateTotalPrice(orderItems));
        }

        return buildResponse(orderRepository.save(order));
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        getOrderEntity(id);

        orderRepository.softDeleteById(id, LocalDateTime.now());
    }

    private OrderDetailsResponseDto buildResponse(Order order) {
        UserInfoResponseDto user = userServiceClient.getUserById(order.getUserId());

        return orderDetailsMapper.toResponse(orderMapper.toResponse(order), user);
    }

    private Order getOrderEntity(UUID id) {
        return orderRepository.findById(id)
                .filter(order -> !Boolean.TRUE.equals(order.getDeleted()))
                .orElseThrow(() -> new OrderNotFoundException(id));
    }

    private OrderItem buildOrderItem(OrderItemRequestDto request, Order order) {
        Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new ItemNotFoundException(request.getItemId()));

        return OrderItem.builder()
                .order(order)
                .item(item)
                .quantity(request.getQuantity())
                .build();
    }

    private BigDecimal calculateTotalPrice(List<OrderItem> orderItems) {
        return orderItems.stream()
                .map(orderItem -> orderItem.getItem()
                        .getPrice()
                        .multiply(BigDecimal.valueOf(orderItem.getQuantity()))
                )
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}