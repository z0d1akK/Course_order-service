package com.innowise.orderservice.order.service;

import com.innowise.orderservice.order.dto.request.CreateOrderRequestDto;
import com.innowise.orderservice.order.dto.request.OrderFilterRequestDto;
import com.innowise.orderservice.order.dto.request.UpdateOrderRequestDto;
import com.innowise.orderservice.order.dto.response.OrderDetailsResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface OrderService {

    /**
     * Creates a new order for the current user.
     * <p>
     * Calculates total order price, validates items existence
     * and returns created order enriched with user information.
     *
     * @param request request object containing order data
     * @return created order details response
     */
    OrderDetailsResponseDto create(CreateOrderRequestDto request);


    /**
     * Returns order details by identifier.
     * <p>
     * Response contains order information and user information
     * received from User Service.
     *
     * @param id order identifier
     * @return order details response
     */
    OrderDetailsResponseDto getById(UUID id);

    /**
     * Returns paginated list of orders.
     * <p>
     * Supports filtering by creation date range and statuses.
     * Available only for administrators.
     *
     * @param filter filtering parameters
     * @param pageable pagination information
     * @return page of order details responses
     */
    Page<OrderDetailsResponseDto> getAll(OrderFilterRequestDto filter, Pageable pageable);

    /**
     * Returns paginated list of orders belonging to a specific user.
     *
     * @param userId user identifier
     * @param pageable pagination information
     * @return page of order details responses
     */
    Page<OrderDetailsResponseDto> getByUserId(UUID userId, Pageable pageable);

    /**
     * Updates existing order.
     * <p>
     * Recalculates total price when order items are changed.
     *
     * @param id order identifier
     * @param request request object containing updated order data
     * @return updated order details response
     */
    OrderDetailsResponseDto update(UUID id, UpdateOrderRequestDto request);

    /**
     * Performs soft deletion of an order.
     *
     * @param id order identifier
     */
    void delete(UUID id);
}